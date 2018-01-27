# AvicusNetwork
A **massive** maven project of all of our private plugins.

# Plugins:

## Atlas
Runs matches, parses XML, and generates docs.
### Core
The main plugin. This holds the general parsing infrastructure for maps and running matches. 
This module holds the functionality which loads the rest of the external jars.
### Competitive-Objectives
Holds parsing and data classes for all objectives related to competitive.
### Docs-Generator
Holds code for generating the docs.avicus.net documentation files from the documentation in each file.
### Arcade
Holds modules for the arcade gamemodes.
### Walls
Holds the walls module and special kits.

## Atrio
This is the lobby plugin. It handles basic lobby things such as portals and jump pads.

## Hook
### Core
This is used to be where all the DB stuff was, but then Magma came along. It's kinda useless.
### Discord
The legacy discord bot. This runs user registration and the such. This is slowly being moved to the ruby app.
### TeamSpeak
The teamspeak bot. Handles user registration and ranks. Useless as of 12/7/17.
### Bukkit
Bukkit plugin which handles database stuff. This is becoming more and more obsolete as things are being moved to Magma. 
This is compiled after Atlas, so it allows us to use Atlas things. T
his runs on servers without Atlas, however, so checks should be made to ensure Atlas before including classes.

## Magma
### Core
Core API and database connections live here. This runs on every server bukkit/bungee.
### Bungee
The bungee plugin. Handles user registration and MOTDs.
### Bukkit
Core code for most of the stuff we do. Code ranges from API to backpacks and gadgets.

## Mars
The tournament/scrimmage plugin. This runs all of the tournament and scrimmage things.
