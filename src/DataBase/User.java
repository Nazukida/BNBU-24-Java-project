package DataBase;

public abstract class User {
    protected String name;
    protected String ID;
    protected String passWord;
    protected boolean jurisdiction;
    protected String mail;

    public User(String name, String ID, String passWord, String mail) {
        this.name = name;
        this.ID = ID;
        this.passWord = passWord;
        this.jurisdiction = false;
        this.mail = mail;
    }

    public abstract boolean login(String ID, String passWord);

    public String getName() {
        return name;
    }

    public String getID() {
        return ID;
    }

    public String getPassWord() {
        return passWord;
    }

    public boolean isJurisdiction() {
        return jurisdiction;
    }

    public String getMail() {
        return mail;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }
    
    public void setMail(String mail) {
        this.mail = mail;
    }
    
    public void setJurisdiction(boolean jurisdiction) {
        this.jurisdiction = jurisdiction;
    }
}
