package foodcenter.service.proxies.interfaces;

public interface AbstractEntityInterface
{

	public String getId();
	
	public Integer getVersion();
	
	public Boolean isEditable();
	
	public void setEditable(Boolean editable);
	
    public String getImageUrl();

    public void setImageUrl(String imageUrl);
}
