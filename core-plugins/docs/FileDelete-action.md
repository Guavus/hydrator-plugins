# File Delete

Description
-----------
File Delete is a Guavus Enterprise Accelerator that is used to delete a file or files.


Use Case
--------
Consider an example wherein you want to remove a file or files. This can be done by configuring the accelerator as shown in the following sections.

Properties
----------
**path:** The full path of the file or files that need to be deleted. If the path points directly to a file, the file will be removed. If the path points to a directory with no regex specified, the directory and all of its contents will be removed. If a regex is specified, only the files and directories matching that regex will be removed.

**fileRegex:** The wildcard regular expression to filter the files in the source directory that will be removed. The fileRegex pattern 
would be a simple search regex pattern which is used in any programming language like -- test.*/.csv -- (for delete csv files start with test). 

**continueOnError:** To specify if the pipeline should continue in case the delete process fails. If all files are not successfully deleted, the action will not re-create the files already deleted.

Example
-------
This example deletes all files ending in `.txt` from `/source/path`:

    {
        "name": "FileDelete",
        "plugin": {
            "name": "FileDelete",
            "type": "action",
            "artifact": {
                "name": "core-plugins",
                "version": "1.4.0-SNAPSHOT",
                "scope": "SYSTEM"
            },
            "properties": {
                "path": "hdfs://example.com:8020/source/path",
                "fileRegex": ".*\.txt",
                "continueOnError": "false"
            }
        }
    }
