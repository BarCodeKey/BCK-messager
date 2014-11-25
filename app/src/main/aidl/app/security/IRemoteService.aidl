// IRemoteService.aidl
package app.security;


interface IRemoteService {

    byte[] encrypt(String type,in byte[] data,String uri);
    byte[] decrypt(String type,in byte[] data,String uri);
}
