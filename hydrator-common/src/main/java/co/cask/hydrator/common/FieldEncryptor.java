/*
 * Copyright © 2016 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package co.cask.hydrator.common;

import co.cask.cdap.api.common.Bytes;
import co.cask.cdap.api.data.schema.Schema;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.IvParameterSpec;

/**
 * Encrypts and decrypts fields based on their schema.
 */
public abstract class FieldEncryptor {
  private static final Logger LOG = LoggerFactory.getLogger(FieldEncryptor.class);
  private final KeystoreConf conf;
  private int mode;
  private Cipher cipher;

  // NOTE: Assuming only RSA asymmetric transformation is used.
  private static final String[] ASYMMETRIC_ALGORITHMS = {"RSA"};

  public FieldEncryptor(KeystoreConf conf, int mode) {
    this.mode = mode;
    this.conf = conf;
  }

  public void initialize() throws Exception {
    KeyStore keystore = KeyStore.getInstance(conf.getKeystoreType());
    try (InputStream keystoreStream = getKeystoreInputStream(conf.getKeystorePath())) {
      keystore.load(keystoreStream, conf.getKeystorePassword().toCharArray());
    }

    cipher = Cipher.getInstance(conf.getTransformation());
    Key key = getCipherKey(keystore, mode, conf);

    if (conf.getIvHex() != null) {
      byte[] ivBytes = Hex.decodeHex(conf.getIvHex().toCharArray());
      IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
      cipher.init(mode, key, ivParameterSpec);
    } else {
      cipher.init(mode, key);
    }

    LOG.debug("Using cipher algorithm: {}", cipher.getAlgorithm());
  }

  /**
   * Returns the key to encrypt/decrypt the data.
   * In case of asymmetric algorithm, it returns public key for encryption and private key for decryption.
   * @param keystore Keystore that contains the keys.
   * @param mode Mode of operation encryption or decryption.
   * @param conf KeyStoreConf configuration object.
   * @return Key for encryption/decryption.
   * @throws Exception
   */
  public static Key getCipherKey(KeyStore keystore, int mode, KeystoreConf conf) throws Exception {
    Key cipherKey = null;

    if(isAsymmetricAlgorithm(conf.getTransformation())) {
      LOG.debug("Getting key for Asymmetric algorithm");
      KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) keystore.getEntry(conf.getKeyAlias(), new KeyStore.PasswordProtection(conf.getKeystorePassword().toCharArray()));
      if(mode == Cipher.ENCRYPT_MODE) {
        cipherKey = pkEntry.getCertificate().getPublicKey();
      } else if(mode == Cipher.DECRYPT_MODE) {
        cipherKey = pkEntry.getPrivateKey();
      }
    } else {
      cipherKey = keystore.getKey(conf.getKeyAlias(), conf.getKeyPassword().toCharArray());
    }
    return cipherKey;
  }

  /**
   * Checks if provided encryption/decryption algorithm is Asymmetric. It does not check whether algorithm is valid or not.
   * @param algorithm Algorithm name. It can be in the format of Algorithm/mode/padding
   * @return
   */
  private static boolean isAsymmetricAlgorithm(String algorithm) {
    if(null == algorithm) {
      throw new IllegalArgumentException("Encyption/Decryption algorithm can not be null. ");
    }
    boolean isAsymmetric = false;
    for(String asymmAlgo: ASYMMETRIC_ALGORITHMS) {
      if(algorithm.trim().startsWith(asymmAlgo)) {
        isAsymmetric = true;
      }
    }

    return isAsymmetric;
  }

  public abstract InputStream getKeystoreInputStream(String keystorePath) throws Exception;

  public byte[] encrypt(Object fieldVal, Schema fieldSchema) throws BadPaddingException, IllegalBlockSizeException {
    if (fieldVal == null) {
      return null;
    }

    Schema.Type fieldType = fieldSchema.isNullable() ? fieldSchema.getNonNullable().getType() : fieldSchema.getType();
    byte[] fieldBytes;
    switch (fieldType) {
      case INT:
        fieldBytes = Bytes.toBytes((int) fieldVal);
        break;
      case LONG:
        fieldBytes = Bytes.toBytes((long) fieldVal);
        break;
      case FLOAT:
        fieldBytes = Bytes.toBytes((float) fieldVal);
        break;
      case DOUBLE:
        fieldBytes = Bytes.toBytes((double) fieldVal);
        break;
      case STRING:
        fieldBytes = Bytes.toBytes((String) fieldVal);
        break;
      case BYTES:
        fieldBytes = (byte[]) fieldVal;
        break;
      case BOOLEAN:
        fieldBytes = Bytes.toBytes((boolean) fieldVal);
        break;
      default:
        throw new IllegalArgumentException("field type " + fieldType + " is not supported.");
    }
    return cipher.doFinal(fieldBytes);
  }

  public Object decrypt(byte[] fieldBytes, Schema fieldSchema) throws BadPaddingException, IllegalBlockSizeException {
    if (fieldBytes == null) {
      return null;
    }

    Schema.Type fieldType = fieldSchema.isNullable() ? fieldSchema.getNonNullable().getType() : fieldSchema.getType();
    fieldBytes = cipher.doFinal(fieldBytes);
    switch (fieldType) {
      case INT:
        return Bytes.toInt(fieldBytes);
      case LONG:
        return Bytes.toLong(fieldBytes);
      case FLOAT:
        return Bytes.toFloat(fieldBytes);
      case DOUBLE:
        return Bytes.toDouble(fieldBytes);
      case STRING:
        return Bytes.toString(fieldBytes);
      case BYTES:
        return fieldBytes;
      case BOOLEAN:
        return Bytes.toBoolean(fieldBytes);
      default:
        throw new IllegalArgumentException("field type " + fieldType + " is not supported.");
    }
  }

}
