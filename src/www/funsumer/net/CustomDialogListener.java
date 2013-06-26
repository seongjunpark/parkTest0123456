package www.funsumer.net;

public interface CustomDialogListener {

    public void onOK(int type);
    public void onOK(int type, String value);
    public void onCancel(int type);
    public void onClose(int type);
    
}