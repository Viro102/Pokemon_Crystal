package sk.uniza.fri.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import sk.uniza.fri.character.Player;
import sk.uniza.fri.pokemon.Pokedex;
import sk.uniza.fri.pokemon.Pokemon;

public class GameScreen implements Screen {
    private final GameClass game;
    private final Stage environmentStage;
    private final Player player;
    private final Pokedex pokedex;
    private final Skin skin;
    private final GameController gameController;
    private final SettingsScreen settingsScreen;
    private Stage pauseStage;
    private boolean isPaused;
    private Zone zone;
    private String currentZone;
    private Array<RectangleMapObject> collisionObjects;
    private Array<RectangleMapObject> exitHitboxes;
    private Array<RectangleMapObject> pokemonSpawnAreas;
    private TiledMapRenderer mapRenderer;

    public GameScreen(GameClass game, Skin skin, Player player, Pokedex pokedex, SettingsScreen settingsScreen) {
        this.game = game;
        this.skin = skin;
        this.player = player;
        this.pokedex = pokedex;
        this.settingsScreen = settingsScreen;
        this.gameController = new GameController(this.player, this);
        this.environmentStage = new Stage(new FitViewport(Constants.MAP_SIZE, Constants.MAP_SIZE));

        this.environmentStage.getCamera().position.set(Constants.MAP_SIZE / 2f, Constants.MAP_SIZE / 2f, 0);
        this.environmentStage.getCamera().update();
        this.environmentStage.addActor(this.player);

        this.zone = new Zone("starter_town", this.player, this.pokedex);

        this.currentZone = this.zone.getMapName();

        this.mapRenderer = new OrthogonalTiledMapRenderer(this.zone.getTiledMap(), this.environmentStage.getBatch());

        this.initZone();
    }

    public void initZone() {
        if (this.collisionObjects != null) {
            this.collisionObjects.clear();
        }
        if (this.exitHitboxes != null) {
            this.exitHitboxes.clear();
        }
        if (this.pokemonSpawnAreas != null) {
            this.pokemonSpawnAreas.clear();
        }

        this.collisionObjects = this.zone.getCollisionObjects();
        this.pokemonSpawnAreas = this.zone.getPokemonSpawnAreas();
        this.exitHitboxes = this.zone.getExitHitboxes();
    }

    public void switchZone(String nextZone) {
        this.zone.getTiledMap().dispose();
        this.environmentStage.clear();
        this.environmentStage.addActor(this.player);

        String prevZone = this.currentZone;
        this.currentZone = nextZone;

        this.zone = new Zone(nextZone, this.player, this.pokedex);
        this.mapRenderer = new OrthogonalTiledMapRenderer(this.zone.getTiledMap(), this.environmentStage.getBatch());

        this.initZone();

        this.zone.getPokemons().keySet().forEach(this.environmentStage::addActor);

        if (this.currentZone.equals("starter_forest") && prevZone.equals("starter_town")) {
            this.player.setPosition(55, 462);
        }
        if (this.currentZone.equals("starter_town") && prevZone.equals("starter_forest")) {
            this.player.setPosition(723, 610);
        }
    }

    public void pauseGame() {
        this.isPaused = true;

        this.pauseStage = new Stage(new ScreenViewport());
        Table pauseTable = new Table();
        pauseTable.setFillParent(true);
        this.pauseStage.addActor(pauseTable);

        TextButton pokedexButton = new TextButton("Pokedex", this.skin);
        TextButton pokemonButton = new TextButton("Pokemon", this.skin);
        TextButton bagButton = new TextButton("Bag", this.skin);
        TextButton playerButton = new TextButton(this.player.getName(), this.skin);
        TextButton saveButton = new TextButton("Save", this.skin);
        TextButton optionsButton = new TextButton("Options", this.skin);
        TextButton exitButton = new TextButton("Exit", this.skin);

        pokedexButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //TODO pokedex
            }
        });

        pokemonButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //TODO party tab
            }
        });

        bagButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //TODO inventory
            }
        });

        playerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO player info
            }
        });

        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //TODO save the game
            }
        });
        optionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                GameScreen.this.game.setScreen(GameScreen.this.settingsScreen);
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        ButtonGroup<TextButton> buttonGroup = new ButtonGroup<>(pokedexButton, pokemonButton, bagButton, playerButton, saveButton, optionsButton, exitButton);

        for (TextButton button : buttonGroup.getButtons()) {
            pauseTable.add(button).width(200);
            pauseTable.row().uniform();
        }

        pauseTable.setPosition(0, Gdx.graphics.getHeight());
        pauseTable.addAction(Actions.moveTo(0, 0, 1, Interpolation.sine));

        Gdx.input.setInputProcessor(this.pauseStage);
    }

    public void resumeGame() {
        this.isPaused = false;
    }

    public boolean isPaused() {
        return this.isPaused;
    }

    public void startBattle(Pokemon enemyPokemon) {
        this.game.setScreen(new BattleScreen(this.game, this, this.skin, this.player, enemyPokemon));
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);

        this.gameController.handleInput();
        this.gameController.checkCollisions(delta, this.collisionObjects, this.exitHitboxes, this.zone.getPokemons());

        this.player.update(delta);

        this.environmentStage.getCamera().update();
        this.environmentStage.getViewport().apply();

        this.mapRenderer.setView((OrthographicCamera) this.environmentStage.getCamera());
        this.mapRenderer.render();

        this.environmentStage.act(delta);
        this.environmentStage.draw();

        if (this.isPaused) {
            this.pauseStage.getViewport().apply();
            this.pauseStage.act(delta);
            this.pauseStage.draw();
        }

        if (Constants.DEBUG && Gdx.input.isKeyPressed(Input.Keys.Z)) {
            this.game.setScreen(new BattleScreen(this.game, this, this.skin, this.player, this.pokedex.getPokemon("bulbasaur")));
        }
    }

    @Override
    public void resize(int width, int height) {
        this.environmentStage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        this.environmentStage.dispose();
        this.skin.dispose();
    }

    @Override
    public void pause() {
        // unused
    }

    @Override
    public void resume() {
        // unused
    }

    @Override
    public void hide() {
        // unused
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this.environmentStage);
    }
}