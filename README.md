# Menu-Processing

A project created for Meals on Wheels of Tarrant County. This project aimed to create a program to automize processing of client menus.
Processes include detecting client markings on the scanned menu, extraction of text identifying which menu belongs to which client, and creation of a user interface. The menu was also redesigned as part of this project.

# Text Extraction
Initially we attempted to extract handwritten text from the menus to assign each menu to each client. This involved pulling names/addresses, but proved to be very innacurate. As part of the menu redesign we attached each client's ID number to the menu and extracted that to identify each client. We also had to extract the dates from each menu to assign the client's selected meal to the day it was to be delivered.
All text extraction processes utilized Google's Tesseract.

# Detecting Markings
To detect which checkbox the client had marked we utilized canny edge detection and Harris corner detection. These processes gave use the coordinates of the check boxes where a flood fill algorith was used to text pixel color. Based off the resulting pixel colors we could determine whether or not the checkbox had been marked.
