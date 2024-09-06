package xyz.hewkawar.qrender;

import android.content.Intent;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;

public class ScanTileService extends TileService {
    @Override
    public void onTileAdded() {
        super.onTileAdded();
        Tile tile = getQsTile();
        tile.setLabel(getString(R.string.scan));
        tile.setState(Tile.STATE_INACTIVE);
        tile.updateTile();
    }

    @Override
    public void onClick() {
        super.onClick();

        Tile tile = getQsTile();

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("WithScanner", true);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        tile.setState(Tile.S);
        tile.setLabel(getString(R.string.scan));

        tile.updateTile();
    }
}
