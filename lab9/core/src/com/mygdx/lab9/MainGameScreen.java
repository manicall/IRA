package com.mygdx.lab9;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class MainGameScreen implements Screen {
    private World world; //переменная для управления миром
    private Box2DDebugRenderer rend; //отладочный отрисовщик тел Мира
    private OrthographicCamera camera; //видеокамера
    private Body body; //тело прямоугольника
    private float Nf;


    @Override
    public void show() {
        //Создание нового мира – задан вектор гравитации в Мире
        world = new World(new Vector2(0, -10), true);
        //Создать камеру с охватом холста 20х15 метров
        camera = new OrthographicCamera(20, 15);
        //Позиционировать камету по центру холста
        camera.position.set(new Vector2(10, 7.5f), 0);
        //Обновление состояния камеры
        camera.update();
        //Создать отладочный отрисовщик
        rend = new Box2DDebugRenderer();

        // Вызвать процедуру создания контуров внешних стен
        createWall();
    }

    float xMax = 20;
    float yMax = 15;

    //Процедура создания внешних стен
    private void createWall() {
        BodyDef bDef= new BodyDef();
        bDef.type= BodyDef.BodyType.StaticBody;
        bDef.position.set(0,0);

        Body w = world.createBody(bDef);
        ChainShape shape = new ChainShape();
        //контур стены в виде перевернутой трапеции без основания
        shape.createChain(new Vector2[]{new Vector2(0.1f,yMax),new Vector2(0.1f,0.1f),
                new Vector2(xMax,0.1f),new Vector2(xMax,yMax)});

        FixtureDef fDef=new FixtureDef();
        fDef.shape=shape;
        fDef.friction=0.1f;
        w.createFixture(fDef);
    }


    @Override
    public void render(float delta) {
        //Очистка экрана
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // Отрисовка
        rend.render(world, camera.combined);
        // массив тел
        Array<Body> bodies = new Array<>();
        world.getBodies(bodies);
        //Выполнение расчета нового состояния Мира
        world.step(1 / 60f, 4, 4);

        float maxSize = 0.5f;
        float xSize = rnd(0.1f, maxSize);
        float ySize = rnd(0.1f, maxSize);

        Nf += delta;

        if (Nf > 0.5f) { // выводим новый объект раз в пол секунды
            createRect(BodyDef.BodyType.DynamicBody,
                    new Vector2(rnd(0, xMax - xSize), yMax - ySize),
                    new Vector2(xSize, ySize));
            Nf = 0;
        }

        for (Body body : bodies) {
            if (body.getPosition().y + ySize < 0) {
                world.destroyBody(body);
            }
        }

    }

    // функция возвращаяющая случайное число в заданном диапазоне
    static float rnd(float min, float max) {
        max -= min;
        return (float) (Math.random() * ++max) + min;
    }

    //функция создания тела прямоугольника
    private Body createRect(BodyDef.BodyType type, Vector2 position, Vector2 size) {
        //Структура геометрических свойств тела
        BodyDef bDef = new BodyDef();
        //задать телу тип динамического тела (на него действует гравитация)
        bDef.type = type;
        //задать позицию тела в Мире – в метрах X и Y
        bDef.position.set(position.x, position.y);
        //создание тела в Мире
        body = world.createBody(bDef);

        //Создать эскиз контура тела в виде приямоугольника 2х2 метра
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(size.x, size.y);

        body.createFixture(getCommonFixtureDef(shape));//закрепить свойства за телом
        return body;
    }

    // общие настройки физических свойств  тела
    private FixtureDef getCommonFixtureDef(Shape shape) {
        //Структура физических свойств тела
        FixtureDef fDef = new FixtureDef();
        fDef.shape = shape;      // назначить вид контура тела
        fDef.density = 2;        // назначить плотность тела г/см3
        fDef.restitution = 0.7f; // назначить упругость
        fDef.friction = 0.1f;    // назначить коэффициент трения
        return fDef;
    }


    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        //Удаление всех тел Мира
        Array<Body> bodies = new Array<Body>();
        world.getBodies(bodies);
        for (int i = 0; i < bodies.size; i++) world.destroyBody(bodies.get(i));

        rend.dispose();
        world.dispose();
    }


}
