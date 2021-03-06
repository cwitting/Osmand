package net.osmand.plus.osmedit;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import net.osmand.data.LatLon;
import net.osmand.data.PointDescription;
import net.osmand.plus.OsmandPlugin;
import net.osmand.plus.R;
import net.osmand.plus.activities.MapActivity;
import net.osmand.plus.mapcontextmenu.MenuBuilder;
import net.osmand.plus.mapcontextmenu.MenuController;
import net.osmand.plus.osmedit.OsmBugsLayer.OpenStreetNote;

public class OsmBugMenuController extends MenuController {

	private OsmEditingPlugin plugin;
	private OpenStreetNote bug;

	public OsmBugMenuController(@NonNull MapActivity mapActivity, @NonNull PointDescription pointDescription, @NonNull OpenStreetNote bug) {
		super(new MenuBuilder(mapActivity), pointDescription, mapActivity);
		plugin = OsmandPlugin.getPlugin(OsmEditingPlugin.class);
		this.bug = bug;

		leftTitleButtonController = new TitleButtonController() {
			@Override
			public void buttonPressed() {
				MapActivity activity = getMapActivity();
				if (plugin != null && activity != null) {
					OpenStreetNote bg = getBug();
					if(bg.isOpened()) {
						plugin.getBugsLayer(activity).commentBug(bg, "");
					} else {
						plugin.getBugsLayer(activity).reopenBug(bg, "");
					}
				}
			}
		};
		if(bug.isOpened()) {
			leftTitleButtonController.caption = mapActivity.getString(R.string.poi_dialog_comment);
		} else {
			leftTitleButtonController.caption = mapActivity.getString(R.string.poi_dialog_reopen);
		}
		leftTitleButtonController.updateStateListDrawableIcon(R.drawable.ic_action_note_dark, true);

		rightTitleButtonController = new TitleButtonController() {
			@Override
			public void buttonPressed() {
				MapActivity activity = getMapActivity();
				if (plugin != null && activity != null) {
					plugin.getBugsLayer(activity).closeBug(getBug(), "");
				}
			}
		};
		rightTitleButtonController.caption = mapActivity.getString(R.string.shared_string_close);
		rightTitleButtonController.updateStateListDrawableIcon(R.drawable.ic_action_remove_dark, true);

		updateData();
	}

	@Override
	protected void setObject(Object object) {
		if (object instanceof OpenStreetNote) {
			this.bug = (OpenStreetNote) object;
			updateData();
		}
	}

	@Override
	protected Object getObject() {
		return bug;
	}

	public OpenStreetNote getBug() {
		return bug;
	}

	@Override
	public Drawable getRightIcon() {
		if (bug.isOpened()) {
			return getIcon(R.drawable.ic_action_gabout_dark, R.color.osm_bug_unresolved_icon_color);
		} else {
			return getIcon(R.drawable.ic_action_gabout_dark, R.color.osm_bug_resolved_icon_color);
		}
	}

	@NonNull
	@Override
	public String getTypeStr() {
		return getPointDescription().getTypeName();
	}

	@Override
	public boolean needStreetName() {
		return false;
	}

	@Override
	public void addPlainMenuItems(String typeStr, PointDescription pointDescription, LatLon latLon) {
		String link = "https://www.openstreetmap.org/note/" + bug.getId();
		addPlainMenuItem(R.drawable.ic_action_openstreetmap_logo, null, link, true, true, null);
		super.addPlainMenuItems(typeStr, pointDescription, latLon);
		for (String description : bug.getCommentDescriptionList()) {
			addPlainMenuItem(R.drawable.ic_action_note_dark, null, description, true, false, null);
		}
	}

	@Override
	public void updateData() {
		super.updateData();
		rightTitleButtonController.visible = bug.isOpened();
	}
}