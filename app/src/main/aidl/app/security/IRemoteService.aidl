// IRemoteService.aidl
package app.security;


interface IRemoteService {

    String encrypt(in byte[] data);
    String decrypt(in byte[] data);
}
