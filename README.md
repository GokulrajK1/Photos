# Photos — Android Port 

Authors: Om Patel onp12, Gokulraj Kumarassamy gk570 

### Prompts by Om Patel 

1. "Port my JavaFX Photos model classes (Tag, Photo, Album) to Android-compatible Java. Tag should only support 'person' and 'location' types, store name/value as lowercase trimmed strings, implement Serializable, and override equals/hashCode."

2. "Write a DataManager class for Android that loads and saves a List<Album> using ObjectInputStream/ObjectOutputStream to internal storage (context.getFilesDir()). Include a static cached list, a load method that returns an empty list if the file doesn't exist, a save method, and a findAlbum helper that does case-insensitive matching."

3. "Build the home screen layout (activity_main.xml) with a title TextView, two buttons (New Album and Search), and a RecyclerView for albums. Also build item_album.xml with the album name on the left and photo count on the right."

4. "Write AlbumAdapter for a RecyclerView that displays a list of Album objects. Include click and long-click listeners passed in via a Listener interface. The long-click should be used for rename/delete options."

5. "Write MainActivity. It should load albums via DataManager, display them in the RecyclerView using AlbumAdapter, handle creating a new album via an AlertDialog with an EditText (reject empty names and duplicates), and handle long-click to show Rename/Delete options. Also wire up the Search button to launch SearchActivity."

### Prompts by Gokul Kumarassamy

1. "Build activity_album.xml with the album title at the top, an Add Photo button, and a RecyclerView displayed as a 3-column grid. Also build item_photo.xml as a FrameLayout with an ImageView for the thumbnail (centerCrop scaleType, 180dp height)."

2. "Write PhotoAdapter for a 3-column grid RecyclerView. It should set image URIs from a Photo's uriString field using ImageView.setImageURI. Wrap the URI parse in a try/catch and fall back to a placeholder drawable if it fails. Include click (open photo) and long-click (remove) listeners."

3. "Write AlbumActivity. Use ActivityResultContracts.GetContent to launch the gallery picker for image/* and request persistable URI permission on the result. Add the picked photo to the album, save via DataManager, and notify the adapter. Long-click on a photo should show a Remove option."

4. "Build activity_photo.xml with the photo filling most of the screen, Previous/Next buttons with an index TextView between them for slideshow navigation, a 'Tags' header, a RecyclerView for tags, and Add Tag / Move buttons at the bottom. Also build item_tag.xml with the tag text and a small X button to remove it."

5. "Write PhotoActivity. Render the current photo by index, allow Previous/Next navigation that disables at the album bounds, show a TagAdapter-driven RecyclerView for tags, allow adding a tag via an AlertDialog with a Spinner (person/location) and an EditText for the value, and allow moving the photo to another album via an AlertDialog list of other albums."

6. "Write SearchActivity. The layout has two AutoCompleteTextView fields paired with type spinners (person/location) and an operation spinner (single, AND, OR). Hook up text watchers on both AutoCompleteTextView fields to filter tag values by prefix across all albums. Run the search across every photo in every album, deduplicate results, and display them in a 3-column grid."

### What we wrote ourselves
- All architectural decisions (package layout, persistence strategy, intent extras for navigation).
- Integration: connecting the three activities via Intents and tying the model layer back to the UI.
- AndroidManifest.xml setup and permissions.
- Build configuration (build.gradle.kts).
- Bug fixes during testing (URI permission persistence, edge cases in slideshow navigation, duplicate detection on tag/photo add).