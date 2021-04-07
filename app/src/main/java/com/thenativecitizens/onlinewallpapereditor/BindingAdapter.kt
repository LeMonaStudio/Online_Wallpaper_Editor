package com.thenativecitizens.onlinewallpapereditor

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.text.Html
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import ja.burhanrashid52.photoeditor.PhotoEditorView


@BindingAdapter("imageUri")
fun loadImage(imageView: ShapeableImageView, imageUri: Uri?){
    imageUri?.let {
        Glide.with(imageView.context)
            .load(imageUri)
            .apply(RequestOptions()
                .placeholder(R.drawable.ic_image_loading)
                .error(R.drawable.ic_image_loading_error))
            .into(imageView)
    }
}

@BindingAdapter("imageForEdit")
fun loadImageForEdit(photoEditorView: PhotoEditorView, imageUri: Uri){
    Glide.with(photoEditorView.context)
        .asDrawable()
        .load(imageUri)
        .apply(RequestOptions()
            .placeholder(R.drawable.ic_image_loading)
            .error(R.drawable.ic_image_loading_error))
        .into(object : CustomTarget<Drawable>(){
            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                photoEditorView.source.setImageDrawable(resource)
            }
            override fun onLoadCleared(placeholder: Drawable?) {
                photoEditorView.source.setImageDrawable(placeholder)
            }
        })
}

//For the EditActions to done by the User
@BindingAdapter("editActionType")
fun editActionButton(button: MaterialButton, editActionType: String){
    val ctx: Context = button.context
    when(editActionType){
        "Brush" -> {
            val icon = ContextCompat.getDrawable(ctx, R.drawable.ic_edit_brush)
            button.icon = icon
            button.text = ctx.getString(R.string.edit_brush_btn_text)
        }
        "Text" ->{
            val icon = ContextCompat.getDrawable(ctx, R.drawable.ic_edit_text)
            button.icon = icon
            button.text = ctx.getString(R.string.edit_text_btn_text)
        }
        "Eraser" ->{
            val icon = ContextCompat.getDrawable(ctx, R.drawable.ic_edit_erazer)
            button.icon = icon
            button.text = ctx.getString(R.string.edit_eraser_btn_text)
        }
        "Sticker" ->{
            val icon = ContextCompat.getDrawable(ctx, R.drawable.ic_edit_sticker)
            button.icon = icon
            button.text = ctx.getString(R.string.edit_sticker_btn_text)
        }
        "Emoji" ->{
            val icon = ContextCompat.getDrawable(ctx, R.drawable.ic_edit_emoji)
            button.icon = icon
            button.text = ctx.getString(R.string.edit_emoji_btn_text)
        }
    }
}


@BindingAdapter("colorView")
fun colorList(shapeImageView: ShapeableImageView, colorName: String){
    val drawable = ContextCompat.getDrawable(shapeImageView.context, R.drawable.color_box)
    when(colorName){
        "black" -> drawable?.setTint(ContextCompat.getColor(shapeImageView.context, R.color.black))
        "purple" -> drawable?.setTint(ContextCompat.getColor(shapeImageView.context, R.color.purple_500))
        "teal" -> drawable?.setTint(ContextCompat.getColor(shapeImageView.context, R.color.teal_200))
        "crimson" -> drawable?.setTint(ContextCompat.getColor(shapeImageView.context, R.color.crimson))
        "blue" -> drawable?.setTint(ContextCompat.getColor(shapeImageView.context, R.color.blue))
        "dark blue" -> drawable?.setTint(ContextCompat.getColor(shapeImageView.context, R.color.darK_blue))
        "chartreuse" -> drawable?.setTint(ContextCompat.getColor(shapeImageView.context, R.color.chartreuse))
        "brown" -> drawable?.setTint(ContextCompat.getColor(shapeImageView.context, R.color.brown))
        "dark olive green" -> drawable?.setTint(ContextCompat.getColor(shapeImageView.context, R.color.dark_olive_green))
        "dark green" -> drawable?.setTint(ContextCompat.getColor(shapeImageView.context, R.color.dark_green))
        "green" -> drawable?.setTint(ContextCompat.getColor(shapeImageView.context, R.color.green))
        "dark slate blue" -> drawable?.setTint(ContextCompat.getColor(shapeImageView.context, R.color.dark_slate_blue))
        "dark sea green" -> drawable?.setTint(ContextCompat.getColor(shapeImageView.context, R.color.dark_sea_green))
        "white" -> drawable?.setTint(ContextCompat.getColor(shapeImageView.context, R.color.white))
        "yellow" -> drawable?.setTint(ContextCompat.getColor(shapeImageView.context, R.color.yellow))
        "orange" -> drawable?.setTint(ContextCompat.getColor(shapeImageView.context, R.color.orange))
        "indigo" -> drawable?.setTint(ContextCompat.getColor(shapeImageView.context, R.color.indigo))
        "magenta" -> drawable?.setTint(ContextCompat.getColor(shapeImageView.context, R.color.magenta))
        "violet" -> drawable?.setTint(ContextCompat.getColor(shapeImageView.context, R.color.violet))
        "sky blue" -> drawable?.setTint(ContextCompat.getColor(shapeImageView.context, R.color.sky_blue))
        "red" -> drawable?.setTint(ContextCompat.getColor(shapeImageView.context, R.color.red))
        "papaya whip" -> drawable?.setTint(ContextCompat.getColor(shapeImageView.context, R.color.papaya_whip))
    }
    shapeImageView.setImageDrawable(drawable)
}

//Display emoji unicode as Emoji in TextView
@BindingAdapter("emojiText")
fun emojiView(textview: MaterialTextView, emojiUnicode: String){
    textview.text = HtmlCompat.fromHtml(emojiUnicode, HtmlCompat.FROM_HTML_MODE_LEGACY)
}
