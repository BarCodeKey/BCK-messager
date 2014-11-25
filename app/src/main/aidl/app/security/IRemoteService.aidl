// IRemoteService.aidl
package app.security;


interface IRemoteService {

    byte[] encrypt(String type,in byte[] data,String lookupKey);
    byte[] decrypt(String type,in byte[] data,String lookupKey);
}
