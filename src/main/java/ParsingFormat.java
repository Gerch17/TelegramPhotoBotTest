public class ParsingFormat {
    private String name = new String();

    public ParsingFormat(String name) {
        this.name = name;
    }

    public String parsing()
    {
        char [] temp = name.toCharArray();
        String format = new String();
        for(int i = 0; i < name.length(); i++)
        {
            if (temp[i] == '.') {
                for(int q = i+1; q < name.length(); q++)
                {
                    format += temp[q];
                }
            }
        }
        return format;
    }
}
