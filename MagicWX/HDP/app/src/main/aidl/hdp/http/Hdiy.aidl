// Hdiy.aidl
package hdp.http;

interface Hdiy {
    int InsertDiyList(String path,String type);

    String GetNamebuNum(int num);

    void ChangeNum(int num);

    String getAllChannelInfo();
}
