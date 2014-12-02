// IRemoteService.aidl
package app.security;


interface IRemoteService {

    byte[] encrypt(String keyType,in byte[] data, String lookupKey);
    byte[] decrypt(String keyType,in byte[] data, String lookupKey);
}
