import android.util.Log
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPReply
import java.io.FileInputStream
import java.io.FileOutputStream


class FTPUtils {
    private val TAG = "Connect FTP"
    var mFTPClient: FTPClient? = null

    fun FTPUtils() {
        mFTPClient = FTPClient()
    }

    fun connect(
        host: String?,
        username: String?,
        password: String?,
        port: Int
    ): Boolean {
        return try {
            mFTPClient!!.connect(host, port)
            if (FTPReply.isPositiveCompletion(mFTPClient!!.replyCode)) {
                mFTPClient!!.login(username, password)
                mFTPClient!!.enterLocalPassiveMode()
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun disconnect() {
        mFTPClient!!.logout()
        mFTPClient!!.disconnect()
    }

    fun getDirectory(): String {
        return mFTPClient!!.printWorkingDirectory()
    }

    fun setWorkDirectory(directory: String) {
        mFTPClient!!.changeWorkingDirectory(directory)
    }

    fun getFileListFromDirectory(directory: String?): Array<String?>? {
        return try{
            val ftpFiles = mFTPClient!!.listFiles(directory)
            val fileList = arrayOfNulls<String>(ftpFiles.size)
            for ((i, file) in ftpFiles.withIndex()) {
                val fileName = file.name
                if (file.isFile) {
                    fileList[i] = "(File) $fileName"
                } else {
                    fileList[i] = "(Directory) $fileName"
                }
            }
            fileList
        } catch (e: Exception) {
            Log.d(TAG, e.message)
            null
        }
    }

    fun createDirectory(directory: String?) {
        mFTPClient!!.makeDirectory(directory)
    }

    fun deleteDirectory(directory: String?) {
        mFTPClient!!.removeDirectory(directory)
    }

    fun ftpDeleteFile(file: String?) {
        mFTPClient!!.deleteFile(file)
    }

    fun renameFile(from: String?, to: String?) {
        mFTPClient!!.rename(from, to)
    }

    fun downloadFile(
        srcFilePath: String?,
        desFilePath: String?
    ): Boolean {
        return try {
            mFTPClient!!.setFileType(FTP.BINARY_FILE_TYPE)
            mFTPClient!!.setFileTransferMode(FTP.BINARY_FILE_TYPE)
            val fos = FileOutputStream(desFilePath)
            mFTPClient!!.retrieveFile(srcFilePath, fos)
            fos.close()
            true
        } catch (e: Exception) {
            Log.d(TAG, e.message)
            false
        }
    }

    fun uploadFile(
        srcFilePath: String?,
        desDirectory: String?,
        desFileName: String?
    ): Boolean {
        return try {
            val fis = FileInputStream(srcFilePath)
            setWorkDirectory(desDirectory!!)
            mFTPClient!!.storeFile(desFileName, fis)
            fis.close()
            true
        } catch (e: Exception) {
            Log.d(TAG, e.message)
            false
        }
    }

}
