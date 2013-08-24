package foodcenter.client.callbacks;

public interface ImageUploadedCallback
{
    
    public void updateImage(String url);

    public void updateImage(String url, String width, String height);
}
