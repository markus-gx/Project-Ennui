package at.ennui.backend.information.model;

import java.util.List;

public class PlaceHolder {
    private Object html_attributions;
    private String next_page_token;
    private List<PlaceDTO> results;
    private String status;

    public Object getHtml_attributions() {
        return html_attributions;
    }

    public void setHtml_attributions(Object html_attributions) {
        this.html_attributions = html_attributions;
    }

    public String getNext_page_token() {
        return next_page_token;
    }

    public void setNext_page_token(String next_page_token) {
        this.next_page_token = next_page_token;
    }

    public List<PlaceDTO> getResults() {
        return results;
    }

    public void setResults(List<PlaceDTO> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
