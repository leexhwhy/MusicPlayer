package com.example.event;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.example.controller.*;
import com.example.gui.GUI;
import com.example.gui.MusicUtils;
import com.example.service.SongMenuOperate;
import com.example.service.SongOperate;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.stage.*;
import javafx.util.Duration;
import javafx.stage.Stage;

public class MainAction {
	static Button addlistbtn;
	static TextField tf = new TextField();//扩展输入栏
	static Button btn = new Button("+");//扩展添加按钮
	static HBox hb = new HBox();//扩展横箱
	
	static VBox vb;//指定left中的竖箱
	static IntegerProperty i, s;//指定gui中的目录和大小
	static Button b, f;//指定topandbottom中的后退和前进
	//static ImageView iv;//指定left中的专面
	
	//-----------------------------------------Top------------------------
 	public void back() {
 		PageQueue pq = gui.getPageManager();
 		IntegerProperty i = gui.getIndex(); IntegerProperty s = gui.getSize();
 		int ii = i.get();
 		if(ii < 1)
 			return;
		Page p;int ss = s.get();
		if(ss < 2) 
			return;
		p = pq.backward();
		show(p);
	}
	
	public void fore() {
 		PageQueue pq = gui.getPageManager();
 		IntegerProperty i = gui.getIndex(); IntegerProperty s = gui.getSize();
		int ii = i.get();int ss = s.get();
 		if(ii >= ss - 1)
 			return;
		Page p = pq.forward();
		show(p);
	}
	
	public void search(Button b, TextField tf, ActionEvent e) {//FIXME
		String key = tf.getText(); 
		if(pq.getSize() > 0 
				&& pq.getPage() instanceof Page.SearchPage 
				&& ((Page.SearchPage)pq.getPage()).getKey().equals(key))
			return;
		ArrayList<MusicUtils> sl = searchsong();
		//FIXME
		Page p = giveSearch();
		((Page.SearchPage)p).setKey(key);
		pq.add(p);
		show(p);
	}
	
	public static void refresh() {
		int ii = i.get();
		int ss = s.get();
		if (ii > 0)
			b.setDisable(false);
		else
			b.setDisable(true);
		if (ii < ss - 1)
			f.setDisable(false);
		else
			f.setDisable(true);
	}
	//-----------------------------------------Bottom---------------------
 	public void last(Button b, MediaPlayer mp, ActionEvent e) {//FIXME
		
	}
	
	public void play(Button b, TopAndBottomPageController tbc, ActionEvent e) {//FIXME
		/*
		MediaPlayer mp = tbc.mp;
		boolean atEndOfMedia = tbc.atEndOfMedia;
		
		updateValues(tbc);
		Status status = mp.getStatus();

        if (status == Status.UNKNOWN
                || status == Status.HALTED) {
            System.out.println("Player is in a bad or unknown state, can't play.");
            return;
        }

        if (status == Status.PAUSED
                || status == Status.READY
                || status == Status.STOPPED) {
            // rewind the movie if we're sitting at the end
            if (atEndOfMedia) {
                mp.seek(mp.getStartTime());
                atEndOfMedia = false;
                b.setText(">");
                updateValues(tbc);
            }
            mp.play();
            b.setText("||");
        } else {
            mp.pause();
        }*/
	}
	
	public void next(Button b, MediaPlayer mp, ActionEvent e) {//FIXME
		
	}

	//-----------------------------------------Left-----------------------
	public void local() {
		if(pq.getSize() > 0 && pq.getPage() instanceof Page.LocalPage ) 
			return;
		Page p = giveLocal();
		pq.add(p);
		show(p);
	}
	
	public void musiclist(String key) {
		if(pq.getSize() > 0 && pq.getPage() instanceof Page.MusicListPage ) 
			return;
		Page p = giveMusicList();
		((Page.MusicListPage)p).setKey(key);
		pq.add(p);
		show(p);
	}
	
	public void addMusicList(Button b, ListView<Button> l) {
		String t = b.getText();
		if(t.equals("+")) {
			vb.getChildren().add(2, hb);
			b.setText("x");
		}
		if(t.equals("x")) {
			vb.getChildren().removeAll(hb);
			b.setText("+");
			tf.clear();
		}
	}
	
	public void addLocalMusic() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("打开音乐文件");
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("MP3", "*.mp3"),
				new FileChooser.ExtensionFilter("flac", "*.flac*"),
				new FileChooser.ExtensionFilter("所有文件", "*.*"));
		List<File> selectedFile = fileChooser.showOpenMultipleDialog(new Stage());
		if(selectedFile != null)
			for(File file : selectedFile)
				SongOperate.addSong(file.getAbsolutePath(),"我的最爱");
	}
	
	public void addLocalDirectory() {
		DirectoryChooser fileChooser = new DirectoryChooser();
		fileChooser.setTitle("打开音乐文件夹");
		File selectedFile = fileChooser.showDialog(new Stage());
		if(selectedFile != null) {
			ArrayList<File> fl = new ArrayList<>();
			loopDirectory(selectedFile, fl);
			for(File file : fl)
			SongOperate.addSong(file.getAbsolutePath(),"我的最爱");
		}
	}
	
	private void loopDirectory(File file, ArrayList<File> fl) {
		for(File f : file.listFiles()) {
			if(f.isDirectory()) {
				loopDirectory(f, fl);
			}
			if(f.isFile()) {
				String s = f.getName();
				if(s.matches(".*\\.(?i)mp3$"))
					fl.add(f);
				if(s.matches(".*\\.(?i)flac$"))
					fl.add(f);
			}
		}
	}
	/*
	protected void updateValues(TopAndBottomPageController tbc) {
		MediaPlayer mp = tbc.mp;
		Duration duration = tbc.duration;
		Slider timeSlider = tbc.timeSlider;
		Label playTime = tbc.playTime;
		Slider volumeSlider = tbc.volumeSlider;
		
		
		
		if (playTime != null && timeSlider != null && volumeSlider != null) {
            Platform.runLater(new Runnable() {

                @Override
                public void run() {
                    Duration currentTime = mp.getCurrentTime();
                    playTime.setText(formatTime(currentTime, duration));
                    timeSlider.setDisable(duration.isUnknown());
                    if (!timeSlider.isDisabled() && duration.greaterThan(Duration.ZERO) && !timeSlider.isValueChanging()) {
                        timeSlider.setValue(currentTime.divide(duration.toMillis()).toMillis() * 100.0);
                    }
                    if (!volumeSlider.isValueChanging()) {
                        volumeSlider.setValue((int) Math.round(mp.getVolume() * 100));
                    }
                }
            });
        }
    }
	*/
	private String formatTime(Duration elapsed, Duration duration) {//FIXME
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60 - durationMinutes * 60;

            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);
            } else {
                return String.format("%02d:%02d/%02d:%02d",
                        elapsedMinutes, elapsedSeconds,
                        durationMinutes, durationSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d",
                        elapsedMinutes, elapsedSeconds);
            }
        }
    }

	private class Extension implements EventHandler<ActionEvent>{//FIXME
		@Override
		public void handle(ActionEvent event) {
			if(tf.getText().length() > 0) {
				try {
					String key = tf.getText();
					Button nb = new Button(key);
					SongMenuOperate.addSongMenu(key);
					gui.getLlC().getListView_musicList().getItems().add(nb);
					nb.setOnAction(nbe -> {
						musiclist(nb.getText());//FIXME
					});
					addlistbtn.fire();
				} catch (RuntimeException e2) {
			        Alert _alert = new Alert(Alert.AlertType.ERROR,e2.getMessage(),new ButtonType("返回", ButtonBar.ButtonData.YES));
			        _alert.show();
				}
				
			}
		}
	}
	
	//-----------------------------------------Play------------------------
	public void reverse() {
		back();
	}
	
	//-----------------------------------------Item------------------------
	private Page giveLocal() {
		LocalMusicPageController lmC = null;
		AnchorPane localmusic = null;
		try {
			FXMLLoader lm = new FXMLLoader(GUI.class.getResource("LocalMusicPage.fxml"),
					ResourceBundle.getBundle("ini"));
			localmusic = (AnchorPane) lm.load();
			lmC = lm.getController();
			lmC.initData(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Page.newPage(Controller.LOCAL, localmusic, lmC);
    }
    
	private Page giveMusicList() {
		MusicListPageController mlC = null;
		ScrollPane musiclist = null;
		try {
			FXMLLoader ml = new FXMLLoader(GUI.class.getResource("MusicListPage.fxml"),
					ResourceBundle.getBundle("ini"));
			musiclist = (ScrollPane) ml.load();
			mlC = ml.getController();
			mlC.initData(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Page.newPage(Controller.MUSICLIST, musiclist, mlC);
    }
 
	private Page giveSearch() {
		AnchorPane searchpage = null;
		SearchPageController spC = null;
		try {
			FXMLLoader sp = new FXMLLoader(GUI.class.getResource("SearchPage.fxml"), ResourceBundle.getBundle("ini"));
			searchpage = (AnchorPane) sp.load();
			spC = sp.getController();
			spC.initData(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Page.newPage(Controller.SEARCH, searchpage, spC);
    }
    
	public static boolean presskey(TextField t, KeyEvent e) {
		if(e.getCode() == KeyCode.ENTER) {
			t.deleteNextChar();
			if (t.getText().length() > 0) {
				return true;
			}
		}
		if(e.getCode() == KeyCode.TAB) {
			t.deleteNextChar();
		}
		return false;
	}
	
	public static void show(Page p) {
		if(p instanceof Page.PlayPage) {
			gui.getPermanent().setCenter(gui.getPlaypage());
			gui.getPermanent().leftProperty().set(null);
			refresh();
			return;
		}
		if(p instanceof Page.SettingPage) {
			gui.getPermanent().leftProperty().set(null);
			refresh();
			return;
		}
		gui.getPermanent().setCenter(p.getPage());
		gui.getPermanent().leftProperty().set(gui.getLeftlist());
		refresh();
	}
	
	public static ArrayList<MusicUtils> searchsong(){//FIXME
		ArrayList<MusicUtils> sl = new ArrayList<>();
		
		return sl;
	}
	
	public MainAction(GUI gui) {//FIXME
		MainAction.gui = gui;
		pq = gui.getPageManager();
		vb = gui.getLlC().getVBox_leftMainField();
		i = new SimpleIntegerProperty();
		s = new SimpleIntegerProperty();
		i.bind(gui.getIndex());
		s.bind(gui.getSize());
		b = gui.getTabC().getButton_back();
		f = gui.getTabC().getButton_forward();
		//iv = gui.getLlC().getImageView_albumCover();
		
		addlistbtn = gui.getLlC().getButton_addMusicList();
		hb.getChildren().addAll(tf, btn);
		btn.setPrefHeight(45);btn.setPrefWidth(53);
		tf.setPrefWidth(212);tf.setMaxWidth(212);tf.setPrefHeight(50);tf.setMaxHeight(50);
		btn.setOnAction(new Extension());
		tf.setOnKeyPressed(new EnterAction(tf, btn));
	}
	public static GUI gui;
	public static PageQueue pq;

	public GUI getGui() {
		return gui;
	}
	
	
}
