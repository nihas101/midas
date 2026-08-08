package de.nihas101.midas.api.backup;

public interface BackupService {
    // TODO: Wrap byte[] in output class that abstracts where the return goes
    //  Even better -> Don't have a return and pass in the class that handles the output stream as needed
    byte[] createBackup() throws Exception;
}
