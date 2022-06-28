package com.example.myapp

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var imageView: ImageView

lateinit var buttonSave: Button
    lateinit var buttonShow: Button
    val REQUEST_IMAGE_GALLERY = 11
    lateinit var uri: Uri
var camPath=""
var path=""
    val REQUEST_IMAGE_CAMERA = 12

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.image)
        buttonSave=findViewById(R.id.btnSave)
        buttonShow=findViewById(R.id.btnShow)


        buttonSave.setOnClickListener({
            saveDataInDatabase()
        })
        buttonShow.setOnClickListener({
            showData()
        })



        imageView.setOnClickListener({
            capturePic()
        })


    }

    private fun showData() {
        var value = ImageRepository(this).getData()
        tvCamera.text = value[0].cameraImage
        tvGallery.text = value[0].galleryImage
    }

    private fun saveDataInDatabase() {
        var inputValue = Pictures(path,camPath)
       ImageRepository(this).saveData(inputValue)

    }


    private fun capturePic() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("select Image")
        builder.setMessage("Choose your option")
        builder.setPositiveButton("Gallery") { dialog, which ->
            dialog.dismiss()
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"

            startActivityForResult(intent, REQUEST_IMAGE_GALLERY)

        }
        builder.setNegativeButton("Camera") { dialog, which ->
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePicture ->
                takePicture.resolveActivity(packageManager)?.also {
                    val permission = ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.CAMERA
                    )
                    if (permission != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(android.Manifest.permission.CAMERA),
                            1
                        )


                    } else {
                        camera()
                    }
                }
            }
            dialog.dismiss()

        }
        var dialog: AlertDialog = builder.create()
        dialog.show()


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK && data != null) {
            imageView.setImageURI(data.data)

            uri = data.data!!
             path = getRealPathFromURI(uri)

            Toast.makeText(this, "get path $path", Toast.LENGTH_LONG).show()

        } else
            if (requestCode == REQUEST_IMAGE_CAMERA && resultCode == RESULT_OK && data != null) {
                val photoPath: String = Uri.parse(camPath).path!!
                val bitmap = BitmapFactory.decodeFile(photoPath)
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap)
                }

            }
    }
    @SuppressLint("QueryPermissionsNeeded")
    fun camera() {
        val camera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (camera.resolveActivity(this.packageManager) != null) {
            val photoFile = getOutputMediaFile(1)
            if (photoFile != null) {
                val photoURI: Uri
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    photoURI = FileProvider.getUriForFile(
                        this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        photoFile
                    )
                    camPath = "file:" + photoFile.absolutePath
                } else {
                    photoURI = Uri.fromFile(photoFile)
                    camPath = photoURI.path!!
                }
                camera.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult( camera, 1, null)
            }
        }
    }
    private fun getOutputMediaFile(type: Int): File? {
        val folder = File(this.obbDir,"Camera Image")
        folder.mkdirs()
        @SuppressLint("SimpleDateFormat") val timeStamp: String =
            SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val mediaFile: File
        if (type == MEDIA_TYPE_IMAGE) {

            mediaFile = File(
                folder.path + File.separator +
                         "_" + timeStamp + ".jpg"
            )
            Log.d("mediaFile", "" + mediaFile)
        } else {
            Log.d("hsfh", "Bad signature!")
            return null
        }
        return mediaFile
    }

    private fun getRealPathFromURI(uri: Uri): String {

        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor = managedQuery(uri, projection, null, null, null)
        val column_index: Int = cursor
            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)


    }


}

