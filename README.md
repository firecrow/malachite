# Malachite

Malachite is a home-screen launcher for android featuring minimal icons. It is the super-seeding project for [Windmill](https://github.com/firecrow/windmill) which was a more fully featured combination of productivity features.

![Screenshot](/docs/screenshot.jpg)

# Purpose

Modifications to my phone for personal use are included here. I've redone the layout to use basic calcualtions instead of the heavier `Adapter` style ui-composition that is more common in Android Studio development projects.

# Code Structure Overview

Malachite is very minimal, implementing it's own layout engine for simplicity and more versatility that the standard android ListAdapter model, which will allow it to grow over time to have more complex layout interactions.

- MainActivity.kt: bootstrap the app, fetch the list of apps, remove their backgrounds, and lay them out in a grid.
- Service.kt: Notification reciever for surfacing important notifications.
    

## Notable Functions of the MainActivity class


    ```kotlin    

    fun getTile(behaviour: Int): App {
        // These are tile `types` which adjust how each tile behaves or is presented
    }

    // This is a hard-coded (for now) list of icons to place ahead of the native
    // alphabetical order for quick access
    val fixedPositionMap = mutableMapOf(
    )

    // This forces a color-matrix adjustment for certain apps that look better that way
    val colorAdjMap = mutableMapOf(
        "com.transferwise.android" to COLOR_ADJ_BRIGHT,
    )

    // TODO: for simplicity this behavior is in the main activity, which will
    // expand into several seperate components as the project expands
    class MainActivity : AppCompatActivity() {
        fun generateSystemList(filtered: Boolean): List<App>{
            // This sets up the list of apps to populate for the homescreen.
            // Including blowing out the backgrounds by modifying the AdaptiveIcon
            // object recieved for each app
        }

        fun fillGrid(
            // This lays out the grid and populates the items
        }

        fun generateEditList(): List<App> {
            // This populates the screen with a set of actions such as refresh, or
            // abbreviate
        }

        fun updateAppList(state: Int) {
            // This populates the screen with a subset of all available apps
        }
    }
    ```
