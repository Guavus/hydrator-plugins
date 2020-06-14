package co.cask.hydrator.plugin.common.sftp;

import ch.ethz.ssh2.*;
import ch.ethz.ssh2.sftp.ErrorCodes;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.util.Progressable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Based on implementation posted on Hadoop JIRA.
 * Using Ganymede SSH lib for improved performance.
 *
 * @author wnagele
 * @see https://issues.apache.org/jira/browse/HADOOP-5732
 */
public class SFTPFileSystem extends FileSystem {
    private static Logger logger = LoggerFactory.getLogger(SFTPFileSystem.class);
    private final int MAX_BUFFER_SIZE = 32768;
    private final int DEFAULT_PORT = 22;
    private final String DEFAULT_KEY_FILE = "${user.home}/.ssh/id_rsa";
    private final String DEFAULT_KNOWNHOSTS_FILE = "${user.home}/.ssh/known_hosts";

    private final String PARAM_BUFFER_SIZE = "io.file.buffer.size";
    private final String PARAM_KEY_FILE = "fs.sftp.key.file";
    private final String PARAM_KEY_PASSWORD = "fs.sftp.key.password";
    private final String PARAM_KNOWNHOSTS = "fs.sftp.knownhosts";

    public static final String SFTP_SCHEME = "sftp";
    // Prefixing it with "guauvs" to avoid clash with other hadoop properties
    public static final String SFTP_PATH_TO_READ = "guavus.fs.sftp.path";
    public static final String PARAM_HOST = "fs.sftp.host";
    public static final String PARAM_PORT = "fs.sftp.port";
    public static final String PARAM_USER = "fs.sftp.user";
    public static final String PARAM_PASSWORD = "fs.sftp.password";
    public final static String SFTP_IMPL_KEY = "fs.sftp.impl";

    private Configuration conf;
    private URI uri;
    private SFTPv3Client client;
    private Connection connection;

    // This is older implementation. Keeping it here for future reference.
    @Override
    public void initialize(URI uri, Configuration conf) throws IOException {
        java.util.logging.Logger.getLogger("ch.ethz.ssh2").setLevel(Level.OFF);
        this.uri = uri;
        this.conf = conf;
        this.conf.set(SFTP_IMPL_KEY, this.getClass().getCanonicalName());

        // If no explicit buffer was set use the maximum.
        // Also limit the buffer to the maximum value.
        int bufferSize = conf.getInt(PARAM_BUFFER_SIZE, -1);
        if (bufferSize > MAX_BUFFER_SIZE || bufferSize == -1)
            conf.setInt(PARAM_BUFFER_SIZE, MAX_BUFFER_SIZE);

        setConf(conf);

        try {
            connect();
        } catch (IOException e) {
            // Ensure to close down connections if we fail during initialization
            close();
            logger.error("Unable to Connect to SFTP server");
            throw e;
        }
    }

    public static Path getPath(Configuration conf) {
        try {
            // Instead of creating Path direct from URI using below method to make sure the password is escaped but not the path.
            // This is needed for globbing to work.
            String url = String.join("", SFTP_SCHEME,
                    "://", conf.get(PARAM_USER),
                    ":", URLEncoder.encode(conf.get(PARAM_PASSWORD), "UTF-8"),
                    "@", conf.get(PARAM_HOST),
                    ":", Integer.toString(conf.getInt(PARAM_PORT, 22)),
                    conf.get(SFTP_PATH_TO_READ));

            return new Path(url);

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unable to encode the password", e);
        }
    }

    @Override
    public URI getUri() {
        return uri;
    }


    /**
     * Overriding this method so that we can call our own `buildDTServiceName` function from SFTPSecurityUtil class.
     *
     * @return
     */
    @Override
    public String getCanonicalServiceName() {
        return (getChildFileSystems() == null)
                ? SFTPSecurityUtil.buildDTServiceName(getUri().getHost(), getUri().getPort())
                : null;
    }

    @Override
    public int getDefaultPort() {
        return DEFAULT_PORT;
    }

    protected void connect() throws IOException {
        if (client == null || !client.isConnected()) {
            String host = conf.get(PARAM_HOST);
            int port = conf.getInt(PARAM_PORT, DEFAULT_PORT);
            String key = conf.get(PARAM_KEY_FILE, DEFAULT_KEY_FILE);
            String keyPassword = conf.get(PARAM_KEY_PASSWORD);
            String user = conf.get(PARAM_USER);
            final String password = conf.get(PARAM_PASSWORD);

            connection = new Connection(host, port);
            connection.connect(new ServerHostKeyVerifier() {
                @Override
                public boolean verifyServerHostKey(String hostname, int port, String serverHostKeyAlgorithm, byte[] serverHostKey) throws Exception {
                    // Assuming this verification is not needed as observed in original SFTPFileSystem Implementation done by Hadoop community
                    return true;
                }
            });

            if (password != null) {
                if (connection.isAuthMethodAvailable(user, "password")) {
                    connection.authenticateWithPassword(user, password);
                } else if (connection.isAuthMethodAvailable(user, "keyboard-interactive")) {
                    connection.authenticateWithKeyboardInteractive(user, new InteractiveCallback() {
                        @Override
                        public String[] replyToChallenge(String name, String instruction, int numPrompts, String[] prompt, boolean[] echo) throws Exception {
                            switch (prompt.length) {
                                case 0:
                                    return new String[0];
                                case 1:
                                    return new String[]{password};
                            }
                            throw new IOException("Cannot proceed with keyboard-interactive authentication. Server requested " + prompt.length + " challenges, we only support 1.");
                        }
                    });
                } else {
                    throw new IOException("Server does not support any of our supported password authentication methods");
                }
            } else {
                connection.authenticateWithPublicKey(user, new File(key), keyPassword);
            }

            client = new SFTPv3Client(connection);
        }
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (client != null && client.isConnected())
            client.close();
        if (connection != null)
            connection.close();
    }

    //----------------------------//

    @Override
    public FSDataInputStream open(Path file) throws IOException {
        SFTPInputStream is = openInternal(file);
        return new FSDataInputStream(is);
    }

    @Override
    public FSDataInputStream open(Path file, int bufferSize) throws IOException {
        SFTPInputStream is = openInternal(file);
        return new FSDataInputStream(new BufferedFSInputStream(is, bufferSize));
    }

    private SFTPInputStream openInternal(Path file) throws IOException {
        if (getFileStatus(file).isDir())
            throw new IOException("Path " + file + " is a directory.");

        String path = file.toUri().getPath();
        SFTPv3FileHandle handle = client.openFileRO(path);
        SFTPInputStream is = new SFTPInputStream(handle, statistics);
        return is;
    }

    @Override
    public FSDataOutputStream create(Path file, FsPermission permission, boolean overwrite, int bufferSize, short replication, long blockSize, Progressable progress) throws IOException {
        return createInternal(file, permission, overwrite, SFTPv3Client.SSH_FXF_CREAT | SFTPv3Client.SSH_FXF_WRITE | SFTPv3Client.SSH_FXF_TRUNC);
    }

    @Override
    public FSDataOutputStream append(Path file, int bufferSize, Progressable progress) throws IOException {
        return createInternal(file, null, true, SFTPv3Client.SSH_FXF_WRITE | SFTPv3Client.SSH_FXF_APPEND);
    }

    protected FSDataOutputStream createInternal(Path file, FsPermission permission, boolean overwrite, int flags) throws IOException {
        if (exists(file) && !overwrite)
            throw new IOException("File " + file + " exists");

        Path parent = file.getParent();
        if (!exists(parent))
            mkdirs(parent);

        SFTPv3FileAttributes attrs = null;
        if (permission != null) {
            attrs = new SFTPv3FileAttributes();
            attrs.permissions = new Short(permission.toShort()).intValue();
        }

        String path = file.toUri().getPath();
        SFTPv3FileHandle handle = client.openFile(path, flags, attrs);
        SFTPOutputStream os = new SFTPOutputStream(handle, statistics);
        return new FSDataOutputStream(os, statistics);
    }

    @Override
    public boolean mkdirs(Path file, FsPermission permission) throws IOException {
        if (!exists(file)) {
            Path parent = file.getParent();
            if (parent == null || mkdirs(parent, permission)) {
                String path = file.toUri().getPath();
                client.mkdir(path, permission.toShort());
            }
        }
        return true;
    }

    @Override
    public boolean rename(Path src, Path dst) throws IOException {
        String oldPath = src.toUri().getPath();
        String newPath = dst.toUri().getPath();
        client.mv(oldPath, newPath);
        return true;
    }

    @Override
    public boolean delete(Path file, boolean recursive) throws IOException {
        String path = file.toUri().getPath();
        if (!getFileStatus(file).isDir()) {
            client.rm(path);
            return true;
        }

        FileStatus[] dirEntries = listStatus(file);
        if (dirEntries != null && dirEntries.length > 0 && !recursive)
            throw new IOException("Directory: " + file + " is not empty.");

        for (FileStatus dirEntry : dirEntries)
            delete(dirEntry.getPath(), recursive);

        client.rmdir(path);
        return true;
    }

    @Override
    public boolean delete(Path file) throws IOException {
        return delete(file, false);
    }

    @Override
    public void setTimes(Path file, long mtime, long atime) throws IOException {
        FileStatus status = getFileStatus(file);
        String path = status.getPath().toUri().getPath();
        SFTPv3FileAttributes attrs = client.stat(path);
        attrs.mtime = new Long(mtime / 1000L).intValue();
        attrs.atime = new Long(atime / 1000L).intValue();
        client.setstat(path, attrs);
    }

    @Override
    public FileStatus getFileStatus(Path file) throws IOException {
        if (file.getParent() == null)
            return new FileStatus(-1, true, -1, -1, -1, new Path("/").makeQualified(this));

        try {
            String path = file.toUri().getPath();
            SFTPv3FileAttributes attrs = client.stat(path);
            return getFileStatus(attrs, file);
        } catch (SFTPException e) {
            if (e.getServerErrorCode() == ErrorCodes.SSH_FX_NO_SUCH_FILE)
                throw new FileNotFoundException(file.toString());
            throw e;
        }
    }

    private FileStatus getFileStatus(SFTPv3FileAttributes attrs, Path file) throws IOException {
        long length = attrs.size;
        boolean isDir = attrs.isDirectory();
        long modTime = new Integer(attrs.mtime).longValue() * 1000L;
        long accessTime = new Integer(attrs.atime).longValue() * 1000L;
        FsPermission permission = new FsPermission(new Integer(attrs.permissions).shortValue());
        String user = Integer.toString(attrs.uid);
        String group = Integer.toString(attrs.gid);
        return new FileStatus(length, isDir, -1, -1, modTime, accessTime, permission, user, group, file);
    }

    @Override
    public FileStatus[] listStatus(Path path) throws IOException {
        FileStatus fileStat = getFileStatus(path);
        if (!fileStat.isDir())
            return new FileStatus[]{fileStat};

        List<SFTPv3DirectoryEntry> sftpFiles = client.ls(path.toUri().getPath());
        ArrayList<FileStatus> fileStats = new ArrayList<FileStatus>(sftpFiles.size());
        for (SFTPv3DirectoryEntry sftpFile : sftpFiles) {
            String filename = sftpFile.filename;
            if (!"..".equals(filename) && !".".equals(filename))
                fileStats.add(getFileStatus(sftpFile.attributes, new Path(path, filename).makeQualified(this)));
        }
        return fileStats.toArray(new FileStatus[0]);
    }

    @Override
    public boolean exists(Path file) {
        try {
            return getFileStatus(file) != null;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public boolean isFile(Path file) throws IOException {
        return !getFileStatus(file).isDir();
    }

    @Override
    public void setWorkingDirectory(Path workDir) {
    }

    @Override
    public Path getWorkingDirectory() {
        return null;
    }
}