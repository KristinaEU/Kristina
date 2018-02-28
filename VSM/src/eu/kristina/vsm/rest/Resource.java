package eu.kristina.vsm.rest;

/**
 * @author Gregor Mehlmann
 */
public final class Resource {

    private final String mHost;
    private final String mName;
    private final String mPath;
    private final String mCons;
    private final String mProd;

    public Resource(
            final String host,
            final String name,
            final String path,
            final String inmt,
            final String outmt) {
        mHost = host;
        mName = name;
        mPath = path;
        mCons = inmt;
        mProd = outmt;
    }

    public String getHost() {
        return mHost;
    }

    public String getName() {
        return mName;
    }

    public final String getPath() {
        return mPath;
    }

    public final String getCons() {
        return mCons;
    }

    public final String getProd() {
        return mProd;
    }

    @Override
    public String toString() {
        return "<Resource"
                + " host=\"" + mHost + "\""
                + " name=\"" + mName + "\""
                + " path=\"" + mPath + "\""
                + " cons=\"" + mCons + "\""
                + " prod=\"" + mProd + "\""
                + "/>";
    }
}
