package gui

import tornadofx.App
import tornadofx.importStylesheet

class MyApp: App(Page::class) {
    init {
        importStylesheet(Styles::class)
    }
}
