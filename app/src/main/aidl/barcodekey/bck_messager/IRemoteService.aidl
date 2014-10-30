// IRemoteService.aidl
package barcodekey.bck_messager;


interface IRemoteService {

    int getPid();
    String encrypt(in byte[] data);
    String decrypt(in byte[] data);
}
