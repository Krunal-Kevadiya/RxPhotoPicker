package com.kevadiyakrunalk.commonutils.common;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * The type FileUtil.
 */
public class FileUtil {
    private static FileUtil sSingleton;
    private Context context;
    private final int EOF = -1;
    private final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    private FileUtil(Context ctx) {
        context = ctx;
    }

    /**
     * Gets instance.
     *
     * @param ctx the ctx
     * @return the instance
     */
    public static FileUtil getInstance(Context ctx) {
        if (sSingleton == null) {
            synchronized (FileUtil.class) {
                sSingleton = new FileUtil(ctx);
            }
        }
        return sSingleton;
    }

    /**
     * Delete dir boolean.
     *
     * @param dir the dir
     * @return true boolean
     */
    public boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            int size = children.length;
            for (int i = 0; i < size; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    /**
     * Get bytes from file byte [ ].
     *
     * @param file the file
     * @return byte[] byte [ ]
     * @throws IOException the io exception
     */
    public byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        long length = file.length();

        if (length > Integer.MAX_VALUE) {
            throw new IOException("File is too large");
        }

        byte[] bytes = new byte[(int) length];
        int size = bytes.length;

        int offset = 0;
        int numRead = 0;
        while (offset < size && (numRead = is.read(bytes, offset, size - offset)) >= 0) {
            offset += numRead;
        }

        if (offset < size) {
            throw new IOException("Could not completely read file " + file.getName());
        }
        is.close();
        return bytes;
    }

    /**
     * Read file as string string.
     *
     * @param path the path
     * @return string string
     * @throws FileNotFoundException the file not found exception
     */
    public String readFileAsString(String path) throws FileNotFoundException {
        File file = new File(path);
        StringBuilder fileContents = new StringBuilder((int) file.length());
        Scanner scanner = new Scanner(file);
        String lineSeparator = System.getProperty("line.separator");

        try {
            while (scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine() + lineSeparator);
            }
            return fileContents.toString();
        } finally {
            scanner.close();
        }
    }

    /**
     * Read file as string string.
     *
     * @param file the file
     * @return string string
     * @throws FileNotFoundException the file not found exception
     */
    public String readFileAsString(File file) throws FileNotFoundException {
        return readFileAsString(file.getAbsolutePath());
    }

    /**
     * Copy file.
     *
     * @param srcFile          the src file
     * @param destFile         the dest file
     * @param preserveFileDate the preserve file date
     * @throws IOException the io exception
     */
    public void copyFile(File srcFile, File destFile, boolean preserveFileDate) throws IOException {
        if (srcFile == null) {
            throw new NullPointerException("Source must not be null");
        } else if (destFile == null) {
            throw new NullPointerException("Destination must not be null");
        } else if (!srcFile.exists()) {
            throw new FileNotFoundException("Source \'" + srcFile + "\' does not exist");
        } else if (srcFile.isDirectory()) {
            throw new IOException("Source \'" + srcFile + "\' exists but is a directory");
        } else if (srcFile.getCanonicalPath().equals(destFile.getCanonicalPath())) {
            throw new IOException("Source \'" + srcFile + "\' and destination \'" + destFile + "\' are the same");
        } else {
            File parentFile = destFile.getParentFile();
            if (parentFile != null && !parentFile.mkdirs() && !parentFile.isDirectory()) {
                throw new IOException("Destination \'" + parentFile + "\' directory cannot be created");
            } else if (destFile.exists() && !destFile.canWrite()) {
                throw new IOException("Destination \'" + destFile + "\' exists but is read-only");
            } else {
                if (destFile.exists() && destFile.isDirectory()) {
                    throw new IOException("Destination \'" + destFile + "\' exists but is a directory");
                } else {
                    FileInputStream fis = null;
                    FileOutputStream fos = null;
                    FileChannel input = null;
                    FileChannel output = null;

                    try {
                        fis = new FileInputStream(srcFile);
                        fos = new FileOutputStream(destFile);
                        input = fis.getChannel();
                        output = fos.getChannel();
                        long size = input.size();
                        long pos = 0L;

                        for (long count = 0L; pos < size; pos += output.transferFrom(input, pos, count)) {
                            count = size - pos > 31457280L ? 31457280L : size - pos;
                        }
                    } finally {
                        try {
                            if (output != null) {
                                output.close();
                            }

                            if (fos != null) {
                                fos.close();
                            }

                            if (input != null) {
                                input.close();
                            }

                            if (fis != null) {
                                fis.close();
                            }
                        } catch (IOException var2) {
                        }
                    }

                    if (srcFile.length() != destFile.length()) {
                        throw new IOException("Failed to copy full contents from \'" + srcFile + "\' to \'" + destFile + "\'");
                    } else {
                        if (preserveFileDate) {
                            destFile.setLastModified(srcFile.lastModified());
                        }
                    }
                }
            }
        }
    }

    public void copyFile(String file1, String file2, boolean preserveFileDate) throws IOException {
        File srcFile = new File(file1);
        File destFile = new File(file2);
        if (srcFile == null) {
            throw new NullPointerException("Source must not be null");
        } else if (destFile == null) {
            throw new NullPointerException("Destination must not be null");
        } else if (!srcFile.exists()) {
            throw new FileNotFoundException("Source \'" + srcFile + "\' does not exist");
        } else if (srcFile.isDirectory()) {
            throw new IOException("Source \'" + srcFile + "\' exists but is a directory");
        } else if (srcFile.getCanonicalPath().equals(destFile.getCanonicalPath())) {
            throw new IOException("Source \'" + srcFile + "\' and destination \'" + destFile + "\' are the same");
        } else {
            File parentFile = destFile.getParentFile();
            if (parentFile != null && !parentFile.mkdirs() && !parentFile.isDirectory()) {
                throw new IOException("Destination \'" + parentFile + "\' directory cannot be created");
            } else if (destFile.exists() && !destFile.canWrite()) {
                throw new IOException("Destination \'" + destFile + "\' exists but is read-only");
            } else {
                if (destFile.exists() && destFile.isDirectory()) {
                    throw new IOException("Destination \'" + destFile + "\' exists but is a directory");
                } else {
                    FileInputStream fis = null;
                    FileOutputStream fos = null;
                    FileChannel input = null;
                    FileChannel output = null;

                    try {
                        fis = new FileInputStream(srcFile);
                        fos = new FileOutputStream(destFile);
                        input = fis.getChannel();
                        output = fos.getChannel();
                        long size = input.size();
                        long pos = 0L;

                        for (long count = 0L; pos < size; pos += output.transferFrom(input, pos, count)) {
                            count = size - pos > 31457280L ? 31457280L : size - pos;
                        }
                    } finally {
                        try {
                            if (output != null) {
                                output.close();
                            }

                            if (fos != null) {
                                fos.close();
                            }

                            if (input != null) {
                                input.close();
                            }

                            if (fis != null) {
                                fis.close();
                            }
                        } catch (IOException var2) {
                        }
                    }

                    if (srcFile.length() != destFile.length()) {
                        throw new IOException("Failed to copy full contents from \'" + srcFile + "\' to \'" + destFile + "\'");
                    } else {
                        if (preserveFileDate) {
                            destFile.setLastModified(srcFile.lastModified());
                        }
                    }
                }
            }
        }
    }

    /**
     * Move directory to directory.
     *
     * @param src           the src
     * @param destDir       the dest dir
     * @param createDestDir the create dest dir
     * @throws IOException the io exception
     */
    public void moveDirectoryToDirectory(File src, File destDir, boolean createDestDir) throws IOException {
        if (src == null) {
            throw new NullPointerException("Source must not be null");
        } else if (destDir == null) {
            throw new NullPointerException("Destination directory must not be null");
        } else {
            if (!destDir.exists() && createDestDir) {
                destDir.mkdirs();
            }

            if (!destDir.exists()) {
                throw new FileNotFoundException("Destination directory \'" + destDir + "\' does not exist [createDestDir=" + createDestDir + "]");
            } else if (!destDir.isDirectory()) {
                throw new IOException("Destination \'" + destDir + "\' is not a directory");
            } else {
                File finalDestDir = new File(destDir, src.getName());

                boolean rename = src.renameTo(finalDestDir);
                if (!rename) {
                    if (finalDestDir.getCanonicalPath().startsWith(src.getCanonicalPath())) {
                        throw new IOException("Cannot move directory: " + src + " to a subdirectory of itself: " + destDir);
                    }

                    copyDirectory(src, finalDestDir);
                    deleteDirectory(src);
                    if (src.exists()) {
                        throw new IOException("Failed to delete original directory \'" + src + "\' after copy to \'" + finalDestDir + "\'");
                    }
                }
            }
        }
    }

    /**
     * Is symlink boolean.
     *
     * @param file the file
     * @return boolean boolean
     * @throws IOException the io exception
     */
    public boolean isSymlink(File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("File must not be null");
        } else {
            File fileInCanonicalDir = null;
            if (file.getParent() == null) {
                fileInCanonicalDir = file;
            } else {
                File canonicalDir = file.getParentFile().getCanonicalFile();
                fileInCanonicalDir = new File(canonicalDir, file.getName());
            }

            return !fileInCanonicalDir.getCanonicalFile().equals(fileInCanonicalDir.getAbsoluteFile());
        }
    }

    /**
     * Delete directory.
     *
     * @param directory the directory
     * @throws IOException the io exception
     */
    public void deleteDirectory(File directory) throws IOException {
        if (directory.exists()) {
            if (!isSymlink(directory)) {
                cleanDirectory(directory);
            }

            if (!directory.delete()) {
                String message = "Unable to delete directory " + directory + ".";
                throw new IOException(message);
            }
        }
    }

    /**
     * Clean directory.
     *
     * @param directory the directory
     * @throws IOException the io exception
     */
    public void cleanDirectory(File directory) throws IOException {
        String var9;
        if (!directory.exists()) {
            var9 = directory + " does not exist";
            throw new IllegalArgumentException(var9);
        } else if (!directory.isDirectory()) {
            var9 = directory + " is not a directory";
            throw new IllegalArgumentException(var9);
        } else {
            File[] files = directory.listFiles();
            if (files == null) {
                throw new IOException("Failed to list contents of " + directory);
            } else {
                IOException exception = null;
                File[] arr$ = files;
                int len$ = files.length;

                for (int i$ = 0; i$ < len$; ++i$) {
                    File file = arr$[i$];

                    try {
                        forceDelete(file);
                    } catch (IOException var8) {
                        exception = var8;
                    }
                }

                if (null != exception) {
                    throw exception;
                }
            }
        }
    }

    /**
     * Force delete.
     *
     * @param file the file
     * @throws IOException the io exception
     */
    public void forceDelete(File file) throws IOException {
        if (file.isDirectory()) {
            deleteDirectory(file);
        } else {
            boolean filePresent = file.exists();
            if (!file.delete()) {
                if (!filePresent) {
                    throw new FileNotFoundException("File does not exist: " + file);
                }

                String message = "Unable to delete file: " + file;
                throw new IOException(message);
            }
        }
    }

    /**
     * Copy directory.
     *
     * @param srcDir  the src dir
     * @param destDir the dest dir
     * @throws IOException the io exception
     */
    public void copyDirectory(File srcDir, File destDir) throws IOException {
        copyDirectory(srcDir, destDir, true);
    }

    /**
     * Copia un directorio
     *
     * @param srcDir           the src dir
     * @param destDir          the dest dir
     * @param preserveFileDate the preserve file date
     * @throws IOException the io exception
     */
    public void copyDirectory(File srcDir, File destDir, boolean preserveFileDate) throws IOException {
        copyDirectory(srcDir, destDir, (FileFilter) null, preserveFileDate);
    }

    /**
     * Copia un directorio
     *
     * @param srcDir  the src dir
     * @param destDir the dest dir
     * @param filter  the filter
     * @throws IOException the io exception
     */
    public void copyDirectory(File srcDir, File destDir, FileFilter filter) throws IOException {
        copyDirectory(srcDir, destDir, filter, true);
    }

    /**
     * Copia un directorio
     *
     * @param srcDir           the src dir
     * @param destDir          the dest dir
     * @param filter           the filter
     * @param preserveFileDate the preserve file date
     * @throws IOException the io exception
     */
    public void copyDirectory(File srcDir, File destDir, FileFilter filter, boolean preserveFileDate) throws IOException {
        if (srcDir == null) {
            throw new NullPointerException("Source must not be null");
        } else if (destDir == null) {
            throw new NullPointerException("Destination must not be null");
        } else if (!srcDir.exists()) {
            throw new FileNotFoundException("Source \'" + srcDir + "\' does not exist");
        } else if (!srcDir.isDirectory()) {
            throw new IOException("Source \'" + srcDir + "\' exists but is not a directory");
        } else if (srcDir.getCanonicalPath().equals(destDir.getCanonicalPath())) {
            throw new IOException("Source \'" + srcDir + "\' and destination \'" + destDir + "\' are the same");
        } else {
            ArrayList exclusionList = null;
            if (destDir.getCanonicalPath().startsWith(srcDir.getCanonicalPath())) {
                File[] srcFiles = filter == null ? srcDir.listFiles() : srcDir.listFiles(filter);
                if (srcFiles != null && srcFiles.length > 0) {
                    exclusionList = new ArrayList(srcFiles.length);
                    File[] arr$ = srcFiles;
                    int len$ = srcFiles.length;

                    for (int i$ = 0; i$ < len$; ++i$) {
                        File srcFile = arr$[i$];
                        File copiedFile = new File(destDir, srcFile.getName());
                        exclusionList.add(copiedFile.getCanonicalPath());
                    }
                }
            }

            doCopyDirectory(srcDir, destDir, filter, preserveFileDate, exclusionList);
        }
    }

    /**
     * Mueve un archivo
     *
     * @param srcFile  the src file
     * @param destFile the dest file
     * @throws IOException the io exception
     */
    public void moveFile(File srcFile, File destFile) throws IOException {
        if (srcFile == null) {
            throw new NullPointerException("Source must not be null");
        } else if (destFile == null) {
            throw new NullPointerException("Destination must not be null");
        } else if (!srcFile.exists()) {
            throw new FileNotFoundException("Source \'" + srcFile + "\' does not exist");
        } else if (srcFile.isDirectory()) {
            throw new IOException("Source \'" + srcFile + "\' is a directory");
        } else if (destFile.exists()) {
            throw new IOException("Destination \'" + destFile + "\' already exists");
        } else if (destFile.isDirectory()) {
            throw new IOException("Destination \'" + destFile + "\' is a directory");
        } else {
            boolean rename = srcFile.renameTo(destFile);
            if (!rename) {
                copyFile(srcFile, destFile, true);
                if (!srcFile.delete()) {
                    forceDelete(destFile);
                    throw new IOException("Failed to delete original file \'" + srcFile + "\' after copy to \'" + destFile + "\'");
                }
            }
        }
    }

    /**
     * Mueve un archivo a un directorio
     *
     * @param srcFile       the src file
     * @param destDir       the dest dir
     * @param createDestDir the create dest dir
     * @throws IOException the io exception
     */
    public void moveFileToDirectory(File srcFile, File destDir, boolean createDestDir) throws IOException {
        if (srcFile == null) {
            throw new NullPointerException("Source must not be null");
        } else if (destDir == null) {
            throw new NullPointerException("Destination directory must not be null");
        } else {
            if (!destDir.exists() && createDestDir) {
                destDir.mkdirs();
            }

            if (!destDir.exists()) {
                throw new FileNotFoundException("Destination directory \'" + destDir + "\' does not exist [createDestDir=" + createDestDir + "]");
            } else if (!destDir.isDirectory()) {
                throw new IOException("Destination \'" + destDir + "\' is not a directory");
            } else {
                moveFile(srcFile, new File(destDir, srcFile.getName()));
            }
        }
    }

    // Private methods
    private void doCopyDirectory(File srcDir, File destDir, FileFilter filter, boolean preserveFileDate, List<String> exclusionList) throws IOException {
        File[] srcFiles = filter == null ? srcDir.listFiles() : srcDir.listFiles(filter);
        if (srcFiles == null) {
            throw new IOException("Failed to list contents of " + srcDir);
        } else {
            if (destDir.exists()) {
                if (!destDir.isDirectory()) {
                    throw new IOException("Destination \'" + destDir + "\' exists but is not a directory");
                }
            } else if (!destDir.mkdirs() && !destDir.isDirectory()) {
                throw new IOException("Destination \'" + destDir + "\' directory cannot be created");
            }

            if (!destDir.canWrite()) {
                throw new IOException("Destination \'" + destDir + "\' cannot be written to");
            } else {
                File[] arr$ = srcFiles;
                int len$ = srcFiles.length;

                for (int i$ = 0; i$ < len$; ++i$) {
                    File srcFile = arr$[i$];
                    File dstFile = new File(destDir, srcFile.getName());
                    if (exclusionList == null || !exclusionList.contains(srcFile.getCanonicalPath())) {
                        if (srcFile.isDirectory()) {
                            doCopyDirectory(srcFile, dstFile, filter, preserveFileDate, exclusionList);
                        } else {
                            copyFile(srcFile, dstFile, preserveFileDate);
                        }
                    }
                }

                if (preserveFileDate) {
                    destDir.setLastModified(srcDir.lastModified());
                }
            }
        }
    }

    /**
     * Create image temp file file.
     *
     * @param filePathDir the file path dir
     * @return the file
     * @throws IOException the io exception
     */
    @SuppressLint("SimpleDateFormat")
    public File createImageTempFile(File filePathDir) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                filePathDir      /* directory */
        );
    }

    /**
     * Create image uri uri.
     *
     * @return the uri
     */
    public Uri createImageUri() {
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues cv = new ContentValues();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        cv.put(MediaStore.Images.Media.TITLE, timeStamp);
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
    }

    /**
     * From file.
     *
     * @param uri the uri
     * @return the file
     * @throws IOException the io exception
     */
    public File from(Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        String fileName = getFileName(uri);
        String[] splitName = splitFileName(fileName);
        File tempFile = File.createTempFile(splitName[0], splitName[1]);
        tempFile = rename(tempFile, fileName);
        tempFile.deleteOnExit();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(tempFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (inputStream != null) {
            copy(inputStream, out);
            inputStream.close();
        }

        if (out != null) {
            out.close();
        }
        return tempFile;
    }

    /**
     * Split file name string [ ].
     *
     * @param fileName the file name
     * @return the string [ ]
     */
    String[] splitFileName(String fileName) {
        String name = fileName;
        String extension = "";
        int i = fileName.lastIndexOf(".");
        if (i != -1) {
            name = fileName.substring(0, i);
            extension = fileName.substring(i);
        }

        return new String[]{name, extension};
    }

    /**
     * Gets file name.
     *
     * @param uri the uri
     * @return the file name
     */
    String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf(File.separator);
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    /**
     * Gets real path from uri.
     *
     * @param contentUri the content uri
     * @return the real path from uri
     */
    String getRealPathFromURIDB(Uri contentUri) {
        Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            String realPath = cursor.getString(index);
            cursor.close();
            return realPath;
        }
    }

    /**
     * Rename file.
     *
     * @param file    the file
     * @param newName the new name
     * @return the file
     */
    File rename(File file, String newName) {
        File newFile = new File(file.getParent(), newName);
        if (!newFile.equals(file)) {
            if (newFile.exists()) {
                if (newFile.delete()) {
                    Logs.getInstance(context).debug("FileUtil", "Delete old " + newName + " file");
                }
            }
            if (file.renameTo(newFile)) {
                Logs.getInstance(context).debug("FileUtil", "Rename file to " + newName);
            }
        }
        return newFile;
    }

    /**
     * Copy int.
     *
     * @param input  the input
     * @param output the outputssss
     * @return the int
     * @throws IOException the io exception
     */
    int copy(InputStream input, OutputStream output) throws IOException {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    /**
     * Copy large long.
     *
     * @param input  the input
     * @param output the output
     * @return the long
     * @throws IOException the io exception
     */
    long copyLarge(InputStream input, OutputStream output) throws IOException {
        return copyLarge(input, output, new byte[DEFAULT_BUFFER_SIZE]);
    }

    /**
     * Copy large long.
     *
     * @param input  the input
     * @param output the output
     * @param buffer the buffer
     * @return the long
     * @throws IOException the io exception
     */
    long copyLarge(InputStream input, OutputStream output, byte[] buffer) throws IOException {
        long count = 0;
        int n;
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    /**
     * Gets real path.
     *
     * @param uri the uri
     * @return the real path
     */
    //get Path
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public String getRealPathFromURI(final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        } else
            return getRealPathFromURIDB(uri);

        return null;
    }

    /**
     * Gets data column.
     *
     * @param uri           the uri
     * @param selection     the selection
     * @param selectionArgs the selection args
     * @return the data column
     */
    public String getDataColumn(Uri uri, String selection,
                                       String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * Is external storage document boolean.
     *
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * Is downloads document boolean.
     *
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * Is media document boolean.
     *
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * Is google photos uri boolean.
     *
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}