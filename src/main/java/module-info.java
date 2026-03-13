/// Module descriptor for the Icon Creator application.
@SuppressWarnings("module") module name.ulbricht.iconcreator {

    requires java.desktop;
    requires java.prefs;

    requires image4j;

    exports name.ulbricht.iconcreator;
}
