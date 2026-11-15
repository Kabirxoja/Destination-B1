package uz.kabirhoja.destination.data.model

import java.io.Serializable

data class TestChoiceItem(val themeName: String, val unitNumber: Int, var checked: Boolean) : Serializable
