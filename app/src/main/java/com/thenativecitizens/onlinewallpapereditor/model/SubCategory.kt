package com.thenativecitizens.onlinewallpapereditor.model

data class SubCategory(
    var subCategoryID: String = "",
    var subCategoryName: String = "",
    var categoryName: String = "",
    var imageUrlList: MutableList<String> = mutableListOf(),
    var subCategoryPlaceholderImageUrl: String = ""
)