package org.rev317.min.api.methods;

import org.parabot.core.Context;
import org.parabot.environment.api.utils.Filter;
import org.rev317.min.Loader;
import org.rev317.min.accessors.Ground;
import org.rev317.min.accessors.SceneObjectTile;
import org.rev317.min.api.wrappers.SceneObject;

import java.util.*;

/**
 * @author Everel, JKetelaar
 */
public class SceneObjects {

    private static final Comparator<SceneObject> NEAREST_SORTER = new Comparator<SceneObject>() {

        @Override
        public int compare(SceneObject n1, SceneObject n2) {
            return n1.distanceTo() - n2.distanceTo();
        }

    };

    private static final Filter<SceneObject> ALL_FILTER = new Filter<SceneObject>() {

        @Override
        public boolean accept(SceneObject object) {
            return true;
        }

    };
    private static HashMap<String, Integer> settings = Context.getInstance().getServerProviderInfo().getSettings();

    /**
     * Gets the most important scene objects in game which can be interacted with, filters out: 'walls, wall
     * decorations, ground decorations'
     *
     * @return scene objects
     */
    public static final SceneObject[] getSceneObjects(Filter<SceneObject> filter) {
        ArrayList<SceneObject> sceneObjects = new ArrayList<>();
        for (int x = 0; x < 104; x++) {
            for (int y = 0; y < 104; y++) {
                final SceneObject sceneObjectAtTile = getSceneObjectAtTile(x, y);
                if (sceneObjectAtTile != null && filter.accept(sceneObjectAtTile)) {
                    sceneObjects.add(sceneObjectAtTile);
                }

            }
        }

        return sceneObjects.toArray(new SceneObject[sceneObjects.size()]);
    }

    /**
     * Gets the most important scene objects in game which can be interacted with
     *
     * @return scene objects
     */
    public static final SceneObject[] getSceneObjects() {
        return getSceneObjects(ALL_FILTER);
    }

    /**
     * Returns array of sceneobjects with the first index to be the nearest
     *
     * @param filter
     * @return sceneobjects
     */
    public static final SceneObject[] getNearest(Filter<SceneObject> filter) {
        final SceneObject[] objects = getSceneObjects(filter);
        Arrays.sort(objects, NEAREST_SORTER);

        return objects;
    }

    /**
     * Returns array of sceneobjects with the first index to be the nearest
     *
     * @return sceneobjects
     */
    public static final SceneObject[] getNearest() {
        return getNearest(ALL_FILTER);
    }

    /**
     * Returns nearest objects with given id
     *
     * @param ids
     * @return sceneobjects
     */
    public static final SceneObject[] getNearest(final int... ids) {
        return getNearest(new Filter<SceneObject>() {

            @Override
            public boolean accept(SceneObject object) {
                for (final int id : ids) {
                    if (id == object.getId()) {
                        return true;
                    }
                }

                return false;
            }

        });
    }

    public static final SceneObject getClosest(final int... ids) {
        SceneObject[] nearestObjects = getNearest(new Filter<SceneObject>() {
            @Override
            public boolean accept(SceneObject object) {
                for (final int id : ids) {
                    if (id == object.getId()) {
                        return true;
                    }
                }

                return false;
            }
        });
        if (nearestObjects == null || nearestObjects.length == 0) {
            return null;
        }

        return nearestObjects[0];
    }

    private static SceneObject getSceneObjectAtTile(int x, int y) {
        Ground sceneTile = Loader.getClient().getScene().getGroundArray()[Game.getPlane()][x][y];
        if (sceneTile == null) {
            return null;
        }

        final SceneObjectTile[] interactiveObjects = sceneTile.getInteractiveObjects();
        if (interactiveObjects != null) {
            for (final SceneObjectTile interactiveObject : interactiveObjects) {
                if (interactiveObject != null) {
                    return new SceneObject(interactiveObject, SceneObject.TYPE_INTERACTIVE);
                }
            }
        }

        SceneObjectTile sceneObjectTile = sceneTile.getWallObject();
        if (sceneObjectTile != null) {
            return new SceneObject(sceneObjectTile, SceneObject.TYPE_WALL);
        }

        return null;
    }

    /**
     * Gets every loaded scene object in game
     *
     * @return every loaded scene object in game
     */
    public static final SceneObject[] getAllSceneObjects() {
        ArrayList<SceneObject> sceneObjects = new ArrayList<>();
        for (int x = 0; x < 104; x++) {
            for (int y = 0; y < 104; y++) {
                final Collection<SceneObject> sceneObjectsAtTile = getSceneObjectsAtTile(x, y);
                if (sceneObjectsAtTile != null && !sceneObjectsAtTile.isEmpty()) {
                    sceneObjects.addAll(sceneObjectsAtTile);
                }
            }
        }

        return sceneObjects.toArray(new SceneObject[sceneObjects.size()]);
    }

    /**
     * Gets all sceneobjects at a tile
     *
     * @param x
     * @param y
     * @return array of sceneobjects, or null if there aren't any
     */
    public static final Collection<SceneObject> getSceneObjectsAtTile(int x, int y) {
        Ground sceneTile = Loader.getClient().getScene().getGroundArray()[Game.getPlane()][x][y];
        ArrayList<SceneObject> sceneObjects = null;
        if (sceneTile != null) {
            final SceneObjectTile[] interactiveObjects = sceneTile.getInteractiveObjects();
            if (interactiveObjects != null) {
                for (final SceneObjectTile interactiveObject : interactiveObjects) {
                    if (interactiveObject != null) {
                        if (sceneObjects == null) {
                            sceneObjects = new ArrayList<>();
                        }
                        sceneObjects.add(new SceneObject(interactiveObject, SceneObject.TYPE_INTERACTIVE));
                    }
                }
            }
            SceneObjectTile sceneObjectTile = sceneTile.getWallObject();
            if (sceneObjectTile != null) {
                if (sceneObjects == null) {
                    sceneObjects = new ArrayList<>();
                }
                sceneObjects.add(new SceneObject(sceneObjectTile, SceneObject.TYPE_WALL));
            }

            sceneObjectTile = sceneTile.getWallDecoration();
            if (sceneObjectTile != null) {
                if (sceneObjects == null) {
                    sceneObjects = new ArrayList<>();
                }
                sceneObjects.add(new SceneObject(sceneObjectTile, SceneObject.TYPE_WALLDECORATION));
            }

            sceneObjectTile = sceneTile.getGroundDecoration();
            if (sceneObjectTile != null) {
                if (sceneObjects == null) {
                    sceneObjects = new ArrayList<>();
                }
                sceneObjects.add(new SceneObject(sceneObjectTile, SceneObject.TYPE_GROUNDDECORATION));
            }

            sceneObjectTile = sceneTile.getGroundItem();
            if (sceneObjectTile != null) {
                if (sceneObjects == null) {
                    sceneObjects = new ArrayList<>();
                }
                sceneObjects.add(new SceneObject(sceneObjectTile, SceneObject.TYPE_GROUNDITEM));
            }
        }

        return sceneObjects;
    }


    public enum Option {
        FIRST(settings.get("menu_scene_object_first_interaction")),
        TALK_TO(settings.get("menu_scene_object_first_interaction")),
        CHOP_DOWN(settings.get("menu_scene_object_first_interaction")),
        CRAFT_RUNE(settings.get("menu_scene_object_first_interaction")),
        PRAY_AT(settings.get("menu_scene_object_first_interaction")),
        OPEN(settings.get("menu_scene_object_first_interaction")),
        DEPOSIT(settings.get("menu_scene_object_first_interaction")),
        USE(settings.get("menu_scene_object_first_interaction")),
        SEARCH(settings.get("menu_scene_object_first_interaction")),
        CLOSE(settings.get("menu_scene_object_first_interaction")),
        CROSS(settings.get("menu_scene_object_first_interaction")),
        MINE(settings.get("menu_scene_object_first_interaction")),
        SMELT(settings.get("menu_scene_object_first_interaction")),

        SECOND(settings.get("menu_scene_object_second_interaction")),
        TELEPORT(settings.get("menu_scene_object_second_interaction")),
        STEAL_FROM(settings.get("menu_scene_object_second_interaction")),
        PRAY(settings.get("menu_scene_object_second_interaction")),
        USE_QUICKLY(settings.get("menu_scene_object_second_interaction")),
        INSPECT(settings.get("menu_scene_object_second_interaction")),

        THIRD(settings.get("menu_scene_object_third_interaction")),

        FOURTH(settings.get("menu_scene_object_fourth_interaction")),
        GUIDE(settings.get("menu_scene_object_fourth_interaction")),

        FIFTH(settings.get("menu_scene_object_fifth_interaction")),

        EXAMINE(settings.get("menu_scene_object_examine"));

        private int actionId;

        Option(int actionId) {
            this.actionId = actionId;
        }

        public int getActionId() {
            return actionId;
        }
    }
}
