# JDETH — A modern love letter to the classic DOOM editor

A complete rewrite of DETH (yes the Doom Editor) from Turbo C into Java, spanning from 2020 to 2025.

*“Thou art the mapper, and the mapper’s job is never done.”* — Some guy in 1995, probably

Welcome to **JDETH** — a Java-powered, cross-platform rewrite inspired by the legendary **DETH** editor. Same attitude, more pixels. It reads your WADs, lets you shape reality on a grid, and spits out fresh **SEGS / SSECTORS / NODES / BLOCKMAP / REJECT** like it’s 1994 and you just discovered texture alignment.

---

## What this is

- ✅ A desktop **map editor** with a fast canvas, an inspector, and the usual DOOM toys.
- ✅ A **WAD toolchain**: read, tweak, write — cleanly.
- ✅ A **node builder hook** so you can (re)build geometry without tears.
- ✅ A tiny **DeHackEd patch editor**, **UMapInfo editor**, and **texture/thing browsers**.

---

## Features you actually care about

- 🧰 **Toolbox & Wizards**: AutoTagBindDialog, CrusherWizardDialog, DehackedEditorDialog, DistributeLightAdvancedDialog, DoorLiftWizardDialog, ExitWizardDialog, FindFilterDialog, LightEffectsDialog, …and more
- 🧠 **Error Checker**: catches zero-length linedefs, broken refs, and other oopsies
- 🧩 **Node Builder**: integrate external builders or use the provided manager
- 🎯 **Thing Palette**: browse, filter, and replace bad guys (or worse — barrels)
- 🧱 **Texture Picker/Browser**: peek, search, slap textures like a pro
- 🧾 **DeHackEd editor**: tweak `Thing`, `Frame`, `Pointer`, `Ammo`, `Weapon`, `Sound`, `Text` blocks
- 🗺️ **UMapInfo**: set map metadata without a ritual
- 🏎️ **Fast Canvas**: pan/zoom, rectangle select, intuitive viewport controls
- 🧹 **No-dup builds**: resets builder state between runs to avoid “double NODES” deja vu

Requires Java 17+ to run.
