/*
 * addremovetiles.cpp
 * Copyright 2013, Thorbjørn Lindeijer <thorbjorn@lindeijer.nl>
 *
 * This file is part of Tiled.
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

#include "addremovetiles.h"

#include "tile.h"
#include "tileset.h"
#include "tilesetdocument.h"

#include <QCoreApplication>

namespace Tiled {
namespace Internal {

AddRemoveTiles::AddRemoveTiles(TilesetDocument *tilesetDocument,
                               Tileset *tileset,
                               const QList<Tile *> &tiles,
                               bool add)
    : mTilesetDocument(tilesetDocument)
    , mTileset(tileset)
    , mTiles(tiles)
    , mTilesAdded(!add)
{
}

AddRemoveTiles::~AddRemoveTiles()
{
    if (!mTilesAdded)
        qDeleteAll(mTiles);
}

void AddRemoveTiles::addTiles()
{
    mTileset->addTiles(mTiles);
    emit mTilesetDocument->tilesetChanged(mTileset);
    mTilesAdded = true;
}

void AddRemoveTiles::removeTiles()
{
    mTileset->removeTiles(mTiles);
    emit mTilesetDocument->tilesetChanged(mTileset);
    mTilesAdded = false;
}


AddTiles::AddTiles(TilesetDocument *tilesetDocument,
                   Tileset *tileset,
                   const QList<Tile *> &tiles)
    : AddRemoveTiles(tilesetDocument, tileset, tiles, true)
{
    setText(QCoreApplication::translate("Undo Commands", "Add Tiles"));
}


RemoveTiles::RemoveTiles(TilesetDocument *tilesetDocument,
                         Tileset *tileset,
                         const QList<Tile *> &tiles)
    : AddRemoveTiles(tilesetDocument, tileset, tiles, false)
{
    setText(QCoreApplication::translate("Undo Commands", "Remove Tiles"));
}

} // namespace Internal
} // namespace Tiled
