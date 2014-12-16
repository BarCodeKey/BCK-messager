// IRemoteService.aidl
package app.security;


interface IRemoteService {

    byte[] encrypt(in byte[] data, String lookupKey);
    byte[] decrypt(in byte[] data, String lookupKey);
}
