package configuration

class ClassPathHandler extends URLStreamHandler {
    private final ClassLoader classLoader;

    public ClassPathHandler(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        final URL resourceUrl = classLoader.getResource(u.getPath());
        if (resourceUrl == null) {
            throw new RuntimeException("Classpath file not found: " + u.getPath())
        }
        resourceUrl.openConnection();
    }

}
