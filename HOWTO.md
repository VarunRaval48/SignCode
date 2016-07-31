# How to use keytool utility for key management

**To generate a new private key with alias and store it in file KEYSTORE_NAME**
> keytool -genkey -alias MYALIAS -keystore KEYSTORE_NAME

**Generate a temporary certificate file**
> keytool -export -alias MYALIAS -file certfile.cer -keystore KEYSTORE_NAME

**To make a Certificate Signing Request(CSR) from existing JAVA Keystore**
> keytool -certreq -alias MYALIAS -keystore KEYSTORE_NAME -file MYALIAS.csr

**To see list of alias in current keystore**
> keytool -list -keystore KEYSTORE_NAME

References:
* [alvinalexander.com](http://alvinalexander.com/java/java-keytool-keystore-certificates)
* [sslshopper.com](https://www.sslshopper.com/article-most-common-java-keytool-keystore-commands.html)
