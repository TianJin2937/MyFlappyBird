package com.jt.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class MyGdxGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture bird[];
	Animation<Texture> birds;
	float stateTime;
	float bv;
	float by;
	Texture tubes[];
	float tx[];
	float ty[];
	int tm[];
	float gap;
	Circle bs;
	Rectangle ts[][];
	ShapeRenderer sr;
	int score;
	BitmapFont sc;
	int gameState;
	Texture endImage;

	private float generateTy() {
		return new Random().nextFloat() * Gdx.graphics.getHeight() / 2 + Gdx.graphics.getHeight() / 6;
	}

	private void reset() {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		bird = new Texture[2];
		bird[0] = new Texture("bird.png");
		bird[1] = new Texture("bird2.png");
		birds = new Animation<Texture>(0.2f, bird);
		stateTime = 0f;
		bv = 0f;
		by = Gdx.graphics.getHeight() / 2 - bird[0].getWidth() / 2;
		gap = Gdx.graphics.getHeight() / 6;
		tubes = new Texture[2];
		tubes[0] = new Texture("toptube.png");
		tubes[1] = new Texture("bottomtube.png");

		tx = new float[]{Gdx.graphics.getWidth(), Gdx.graphics.getWidth() * 1.5f, Gdx.graphics.getWidth() * 2.0f};
		ty = new float[]{generateTy(), generateTy(), generateTy()};
		tm = new int[]{0, 0, 0};

		sr = new ShapeRenderer();
		bs = new Circle();
		ts = new Rectangle[3][2];
		for(int i = 0; i < 3; i++) {
			ts[i][0] = new Rectangle();
			ts[i][1] = new Rectangle();
		}

		score = 0;
		sc = new BitmapFont();
		sc.setColor(Color.WHITE);
		sc.getData().setScale(10);

		gameState = 1;
		endImage = new Texture("gameover.png");
	}

	@Override
	public void create () {
		reset();
	}

	@Override
	public void render () {
		if (gameState == 0) {
			batch.begin();
			batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

			batch.end();
		}

		if (gameState == 1) {
			stateTime += Gdx.graphics.getDeltaTime();
			Texture currentBird = birds.getKeyFrame(stateTime, true);
			by += bv;
			bv -= 0.7;
			if (Gdx.input.justTouched()) {
				bv += 20;
			}
			by = by < 0 ? 0 : by;
			for (int i = 0; i < 3; i++) {
				tx[i] -= 4f;
				if (tx[i] < -tubes[1].getWidth()) {
					tx[i] = 1.5f * Gdx.graphics.getWidth() - tubes[0].getWidth();
					ty[i] = generateTy();
					tm[i] = 0;
				}
			}
			bs.set(Gdx.graphics.getWidth() / 2, by + bird[0].getWidth() / 2, bird[0].getWidth() / 2);
			for (int i = 0; i < 3; i++) {
				ts[i][0].x = tx[i];
				ts[i][0].y = ty[i] + gap;
				ts[i][0].height = tubes[0].getHeight();
				ts[i][0].width = tubes[0].getWidth();

				ts[i][1].x = tx[i];
				ts[i][1].y = ty[i] - tubes[1].getHeight();
				ts[i][1].height = tubes[1].getHeight();
				ts[i][1].width = tubes[1].getWidth();
			}

			for (int i = 0; i < 3; i++) {
				if (Intersector.overlaps(bs, ts[i][0]) || Intersector.overlaps(bs, ts[i][1])) {
					gameState = 2;
				}
			}

			for (int i = 0; i < 3; i++) {
				if (tm[i] == 0 && tx[i] + tubes[0].getWidth() / 2 < Gdx.graphics.getWidth() / 2) {
					score += 1;
					tm[i] = 1;
				}
			}

			batch.begin();
			batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			batch.draw(currentBird, Gdx.graphics.getWidth() / 2 - bird[0].getWidth() / 2, by);
			for (int i = 0; i < 3; i++) {
				batch.draw(tubes[0], tx[i], ty[i] + gap);
				batch.draw(tubes[1], tx[i], ty[i] - tubes[1].getHeight());
			}
			float tw = new GlyphLayout(sc, String.valueOf(score)).width;
			sc.draw(batch, String.valueOf(score), Gdx.graphics.getWidth() / 2 - tw / 2, Gdx.graphics.getHeight() * 5 / 6);
			batch.end();

		/*
		sr.begin(ShapeRenderer.ShapeType.Filled);
		sr.setColor(Color.RED);
		for(int i = 0; i < 3; i++) {
			sr.rect(ts[i][0].x, ts[i][0].y, ts[i][0].width, ts[i][0].height);
			sr.rect(ts[i][1].x, ts[i][1].y, ts[i][1].width, ts[i][1].height);
		}
		sr.end();
		*/
		}

		if (gameState == 2) {
			if(Gdx.input.justTouched()) {
				reset();
				gameState = 1;
			}

			batch.begin();
			batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			batch.draw(endImage, Gdx.graphics.getWidth()/2 - Gdx.graphics.getWidth() / 3 * 2 / 2,
					Gdx.graphics.getHeight() / 2, Gdx.graphics.getWidth() /3 * 2,
					endImage.getHeight() * Gdx.graphics.getWidth() /3 * 2 / endImage.getWidth());
			float tw = new GlyphLayout(sc, String.valueOf(score)).width;
			sc.draw(batch, String.valueOf(score), Gdx.graphics.getWidth() / 2 - tw / 2, Gdx.graphics.getHeight() * 5 / 6);
			batch.end();
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		background.dispose();
	}
}
