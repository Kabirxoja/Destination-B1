package uz.kabirhoja.destination.data.model

data class SpeakerItem(
    val number: Int,
    val name: String,
    val locale: String,
    val quality: Int,
    val latency: Int,
    val requiresNetwork: Boolean
)
