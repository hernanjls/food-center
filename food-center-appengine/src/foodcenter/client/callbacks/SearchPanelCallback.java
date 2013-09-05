package foodcenter.client.callbacks;

import foodcenter.client.callbacks.search.SearchOptions;

public interface SearchPanelCallback<T extends SearchOptions>
{
    public void search(T options);
}
