package net.izsak.teamcity;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

public abstract class SdkDetector {
    public abstract List<SdkVersion> detectSdkVersions();

    protected String combine(String[] s, String glue)
    {
        int k=s.length;
        if (k==0)
            return null;
        StringBuilder out=new StringBuilder();
        out.append(s[0]);
        for (int x=1;x<k;++x)
            out.append(glue).append(s[x]);
        return out.toString();
    }

    protected String combine(Iterator<String> s, String glue) {
        if (!s.hasNext())
            return null;

        StringBuilder out = new StringBuilder();
        out.append(s.next());
        while (s.hasNext()) {
            out.append(glue).append(s.next());
        }
        return out.toString();
    }
}
