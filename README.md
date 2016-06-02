# SignCode
Java Files to sign code using keys in java keystore

##Sign File
Methods to sign file are available in src/stored_keys/GetSigExistingKeys.java
Implementation of these methods is available at src/stored_keys/GenSigExistingKeys.java

##Verify Signature
Methods to verify signature of a filea are available at src/stored_keys/GetVerExistingKeys.java
Implementation of these methods is available at src/stored_keys/VerSigExistKeys.java

##TestScripts
Folder TestScripts contains javascript, python scripts with Valid, Invalid signatures.
Information about invalidity can be seen in commit description.

##Generate Test Scripts
To see how this test scripts are generated, go to src/test_data/GenTestScript.java

##Extract content, signature, certificate
src/signature package contains classes that can be used to import certificate from various places like byte stream, file and JAVA Keystore
