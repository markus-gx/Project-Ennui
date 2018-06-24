package at.ennui.backend.pages.model;

public class PageHolder {
    private boolean success;
    private String msg;
    private int userPagesCount;
    private int userPagesSavedCount;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getUserPagesCount() {
        return userPagesCount;
    }

    public void setUserPagesCount(int userPagesCount) {
        this.userPagesCount = userPagesCount;
    }

    public int getUserPagesSavedCount() {
        return userPagesSavedCount;
    }

    public void setUserPagesSavedCount(int userPagesSavedCount) {
        this.userPagesSavedCount = userPagesSavedCount;
    }
}
