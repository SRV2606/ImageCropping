package com.example.imagecropping

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {
    private val GALLERY_REQUEST_CODE = 1234
    private val TAG: String = "DebugResult"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        update_TV.setOnClickListener {

            pickFromGallery()
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

            GALLERY_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.data?.let { uri ->
                        launchImageCrop(uri, 1)
                    }
                } else {
                    Log.e(TAG, "Image selection error: Couldn't select that image from memory.")
                }
            }

            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    setImage(result.uri)
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Log.e(TAG, "Crop error: ${result.error}")
                }
            }
        }
    }

    private fun setImage(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .into(imageIV)

    }

    private fun launchImageCrop(uri: Uri, imageCropperOption: Int) {

        if (imageCropperOption == 1) {
            CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1920, 1080)
                .setCropShape(CropImageView.CropShape.RECTANGLE) // default is rectangle
                .start(this)
        } else if (imageCropperOption == 2) {
            val file = File(cacheDir, "SampleImage" + ".jpg")

            UCrop.of(uri, Uri.fromFile(file))
                .withAspectRatio(1F, 1F)
                .withMaxResultSize(30, 30)
                .start(this);
        }
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }
}


