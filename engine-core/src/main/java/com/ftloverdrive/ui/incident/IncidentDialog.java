package com.ftloverdrive.ui.incident;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pools;
import com.ftloverdrive.core.OverdriveContext;
import com.ftloverdrive.event.engine.DisposeListener;
import com.ftloverdrive.event.incident.IncidentSelectionEvent;
import com.ftloverdrive.io.OVDSkin;
import com.ftloverdrive.model.incident.Consequence;
import com.ftloverdrive.model.incident.IncidentModel;
import com.ftloverdrive.model.incident.PlotBranch;
import com.ftloverdrive.ui.ShaderLabel;


public class IncidentDialog extends Window implements Disposable, EventListener {

	private static final WindowStyle defaultWindowStyle = new WindowStyle( new BitmapFont(), new Color(), null );
	private static final int maxChoiceCount = 10;

	public static final String SKIN_PATH = "internal://overdrive-assets/skins/incident-dialog/window.json";
	public static final String ACTOR_NAME = "IncidentDialog";

	// The window's last location.
	protected static float lastX = -1;
	protected static float lastTopY = -1;

	protected int sepChoices = 0;
	protected int sepTextConseq = 0;
	protected int sepConseqChoice = 0;

	// Size of a single repeating tile of the background
	protected float frameBodyWidth = 0;
	protected float frameBodyHeight = 0;

	protected Array<DisposeListener> disposeListeners = new Array<DisposeListener>( false, 1 );

	protected OverdriveContext context;
	protected OVDSkin skin;

	protected LabelStyle normalStyle;
	protected LabelStyle hoverStyle;

	protected ShaderLabel lblIncText;
	protected Table choiceTable;
	protected ConsequenceBox conseqBox;

	// Only 10 keys on the numeric keyboard, so we can only support 10 hotkeyed plot branches
	private int choiceCount = 0;
	private PlotBranch[] choices = new PlotBranch[maxChoiceCount];

	private boolean captureInput = false;


	public IncidentDialog( OverdriveContext srcContext ) {
		super( "", defaultWindowStyle );
		context = Pools.get( OverdriveContext.class ).obtain();
		context.init( srcContext );

		align( Align.top );
		setKeepWithinStage( true );
		setMovable( true );
		setName( ACTOR_NAME );

		context.getAssetManager().load( SKIN_PATH, OVDSkin.class );
		context.getAssetManager().finishLoading();
		skin = context.getAssetManager().get( SKIN_PATH, OVDSkin.class );

		LabelStyle incStyle = skin.get( "incident-text", LabelStyle.class );
		normalStyle = skin.get( "choice-normal", LabelStyle.class );
		hoverStyle = skin.get( "choice-hover", LabelStyle.class );

		// DistanceFieldFontShader fontShader = new DistanceFieldFontShader( 1.0f / 8.0f );

		sepChoices = skin.get( "choice-separator", Integer.class );
		sepTextConseq = skin.get( "text-conseq-separator", Integer.class );
		sepConseqChoice = skin.get( "conseq-choice-separator", Integer.class );

		WindowStyle wndStyle = skin.get( "window-style", WindowStyle.class );
		setStyle( wndStyle );

		Drawable d = wndStyle.background;
		frameBodyWidth = d.getMinWidth() - ( d.getLeftWidth() + d.getRightWidth() );
		frameBodyHeight = d.getMinHeight() - ( d.getTopHeight() + d.getBottomHeight() );

		lblIncText = new ShaderLabel( "", incStyle );
		lblIncText.setAlignment( Align.top | Align.left, Align.center | Align.left );
		lblIncText.setWrap( true );
		// lblIncText.setShader( fontShader );
		row().top().expandX().fillX().spaceBottom( sepTextConseq );
		add( lblIncText );

		conseqBox = new ConsequenceBox( skin );
		row().center().spaceBottom( sepConseqChoice );
		add( conseqBox );

		choiceTable = new Table();
		choiceTable.align( Align.left );
		row().expandX().fillX().left();
		add( choiceTable );

		addListener( new IncidentDialogInputListener() );
		setWidth( computePreferredWidth( skin.get( "window-width", Integer.class ) ) );
	}

	public void showConseequenceBox( boolean show ) {
		conseqBox.setVisible( show );
		if ( show ) {
			getCell( lblIncText ).spaceBottom( sepTextConseq );
			getCell( conseqBox ).spaceBottom( sepConseqChoice );
		}
		else {
			getCell( lblIncText ).spaceBottom( sepConseqChoice );
			getCell( conseqBox ).spaceBottom( 0 );
		}
	}

	/**
	 * If set to true, this window will capture all input until it is dismissed.
	 */
	public void setCaptureInput( boolean block ) {
		captureInput = block;
	}

	public void setIncidentText( String text ) {
		lblIncText.setText( text );
		lblIncText.setSize( lblIncText.getPrefWidth(), lblIncText.getPrefHeight() );
	}

	public void addChoice( final PlotBranch branch ) {
		if ( choiceCount < maxChoiceCount )
			choices[choiceCount] = branch;
		choiceCount++;

		StringBuilder buf = new StringBuilder();
		buf.append( choiceCount );
		buf.append( ". " );
		buf.append( branch.getText() );

		final PlotBranchLabel choice = new PlotBranchLabel( buf.toString(), normalStyle, hoverStyle );
		choice.setUserObject( branch );

		final ConsequenceBox cBox = new ConsequenceBox( skin );
		boolean hasConseqs = false;
		if ( branch.isSpoilerVisible() && branch.getIncidentRefId() != -1 ) {
			IncidentModel incModel = context.getReferenceManager().getObject( branch.getIncidentRefId(), IncidentModel.class );
			int[] conseqRefIds = incModel.consequenceRefIds();
			if ( conseqRefIds.length > 0 ) {
				cBox.setTouchable( Touchable.enabled );
				cBox.setUserObject( branch );
				for ( int refId : conseqRefIds ) {
					Consequence conseq = context.getReferenceManager().getObject( refId, Consequence.class );
					conseq.placeConsequenceActor( cBox );
				}
				hasConseqs = true;
			}
		}

		final int conseqPad = 10;
		float availableWidth = getBodyWidth() - ( hasConseqs ? ( cBox.getMinWidth() + conseqPad ) : 0 );
		float w = getLabelWidth( choice, availableWidth );
		choiceTable.row();
		choiceTable.add( choice ).left().width( w ).spaceBottom( sepChoices );

		if ( hasConseqs ) {
			choiceTable.add( cBox ).left().spaceBottom( sepChoices ).spaceLeft( conseqPad );
		}

		InputListener listener = new InputListener() {

			public void enter( InputEvent event, float x, float y, int pointer, Actor fromActor ) {
				choice.setStyle( hoverStyle );
				cBox.setBorderTint( hoverStyle.fontColor );
			}

			public void exit( InputEvent event, float x, float y, int pointer, Actor toActor ) {
				// Exit events are apparently also sent on mouseUp/Down...
				if ( toActor != choice && toActor != cBox ) {
					choice.setStyle( normalStyle );
					cBox.setBorderTint( normalStyle.fontColor );
				}
			}
		};
		choice.addListener( listener );
		cBox.addListener( listener );
	}

	public void addConsequence( Consequence conseq ) {
		conseq.placeConsequenceActor( conseqBox );
	}

	/*
	 * =======================================================================================
	 */

	/**
	 * Computes the preferred width of this window, modifying the width passed in argument so that the
	 * background image is tiled correctly.
	 * 
	 * The value returned is always greater than or equal to the one passed.
	 */
	public float computePreferredWidth( int w ) {
		layout();
		float bw = w - ( getPadLeft() + getPadRight() );
		if ( bw % frameBodyWidth != 0 )
			w += frameBodyWidth - bw % frameBodyWidth;
		return w;
	}

	/**
	 * Computes the preferred height of this window, accounting for all actors currently contained within it.
	 * 
	 * Using the value returned by this function as height for the dialog should also guarantee that the
	 * background image is tiled correctly.
	 */
	public float computePreferredHeight() {
		layout();
		float h = tableHeight( this );
		if ( h % frameBodyHeight != 0 )
			h += frameBodyHeight - h % frameBodyHeight;
		h += getPadTop() + getPadBottom();
		return h;
	}

	private float tableHeight( Table t ) {
		float result = 0;
		Array<Cell> cells = t.getCells();
		for ( int r = 0; r < t.getRows(); ++r ) {
			float maxH = 0;
			for ( int c = 0; c < t.getColumns(); ++c ) {
				int i = r * t.getColumns() + c;
				if ( i >= cells.size )
					continue;
				Cell cell = cells.get( i );
				float pad = cell.getComputedPadTop() + cell.getComputedPadBottom();
				float ch = cell.getMinHeight() + pad;
				if ( ch > maxH )
					maxH = ch;
			}
			result += maxH;
		}
		return result;
	}

	/**
	 * Sets the dialog's position to its preferred position.
	 * This method should be called after the size of the dialog has been set,
	 * and after it has been added to a stage (otherwise it'll throw NPE, since
	 * it uses the stage's size for initial positioning)
	 */
	public void usePreferredPosition() {
		if ( lastX == -1 )
			setPosition( ( getStage().getWidth() - getWidth() ) / 2, ( getStage().getHeight() - getHeight() ) / 2 );
		else
			setPosition( lastX, lastTopY - getHeight() );
	}

	/*
	 * ==========================================================================
	 */

	/**
	 * A "soft dispose", resets the dialog so that it can be reused by another incident.
	 */
	private void reset0() {
		for ( ; choiceCount > 0; ) {
			--choiceCount;
			choices[choiceCount] = null;
		}
		conseqBox.clear();
		// Hide the conseqTable to prevent it from flickering when the dialog is changing
		conseqBox.setVisible( false );
		choiceTable.clear();

		fireDisposedEvent();
		disposeListeners.clear();

		System.gc();
	}

	@Override
	public void dispose() {
		context.getAssetManager().unload( SKIN_PATH );

		Stage stage = getStage();
		stage.getRoot().removeActor( this );
		stage.removeListener( this );

		Pools.get( OverdriveContext.class ).free( context );
	}

	private void fireDisposedEvent() {
		for ( DisposeListener listener : disposeListeners )
			listener.objectDisposed( context, this );
	}

	public void addDisposeListener( DisposeListener listener ) {
		disposeListeners.add( listener );
	}

	public void removeDisposeListener( DisposeListener listener ) {
		disposeListeners.removeValue( listener, true );
	}

	/*
	 * ==========================================================================
	 */

	private float getBodyWidth() {
		return getWidth() - getPadLeft() - getPadRight();
	}

	private float getLabelWidth( Label l, float maxW ) {
		return l.getStyle().font.getWrappedBounds( l.getText(), maxW ).width;
	}

	private void choiceSelected( OverdriveContext context, PlotBranch branch, float x, float y ) {
		int incRefId = branch.getIncidentRefId();
		if ( incRefId == -1 ) {
			// TODO: Send notification that incident ended, to unpause the game?

			// Once the incident chain is finished, reset the size, so that the next incident dialog
			// will appear at the default location.
			lastX = -1;
			lastTopY = -1;
			reset0();
			dispose();
		}
		else {
			// Store the current location of the window so that the new one is created at the same spot
			// There's no need to share this data with the server/other players, so just preserve it statically
			lastX = getX();
			lastTopY = getTop();

			IncidentSelectionEvent incSelectionE = Pools.get( IncidentSelectionEvent.class ).obtain();
			incSelectionE.init( incRefId );
			context.getScreenEventManager().postDelayedEvent( incSelectionE );

			reset0();
		}
	}

	@Override
	public boolean handle( Event event ) {
		boolean handled = getListeners().get( 0 ).handle( event );
		if ( isVisible() && event instanceof InputEvent )
			handled = captureInput;
		return handled;
	}


	private class IncidentDialogInputListener extends InputListener {

		boolean dragging = false;
		float startX = -1, startY = -1;


		@Override
		public boolean keyDown( InputEvent event, int keycode ) {
			int choiceNumber = keycode;

			if ( choiceNumber >= Keys.NUM_0 && choiceNumber <= Keys.NUM_9 ) {
				choiceNumber -= Keys.NUM_0;
				if ( choiceNumber == 0 )
					choiceNumber = maxChoiceCount;
				if ( choiceNumber <= choiceCount )
					choiceSelected( context, choices[choiceNumber - 1], getX(), getTop() );
				return true;
			}

			if ( captureInput )
				return true;
			return super.keyDown( event, keycode );
		}

		@Override
		public boolean touchDown( InputEvent e, float x, float y, int pointer, int button ) {
			Actor target = e.getTarget();
			dragging = false;
			if ( target.isDescendantOf( choiceTable ) && e.getButton() == Buttons.LEFT ) {
				PlotBranch branch = (PlotBranch)target.getUserObject();
				choiceSelected( context, branch, getX(), getTop() );
				return true;
			}
			else if ( e.getButton() == Buttons.LEFT ) {
				dragging = true;
				startX = x;
				startY = y;
				return true;
			}

			if ( captureInput )
				return true;
			return super.touchDown( e, x, y, pointer, button );
		}

		@Override
		public void touchDragged( InputEvent e, float x, float y, int pointer ) {
			if ( dragging ) {
				float width = getWidth(), height = getHeight();
				float windowX = getX(), windowY = getY();

				float amountX = x - startX, amountY = y - startY;
				windowX += amountX;
				windowY += amountY;

				setBounds( Math.round( windowX ), Math.round( windowY ), Math.round( width ), Math.round( height ) );
			}

			if ( !captureInput )
				super.touchDragged( e, x, y, pointer );
		}
	}
}
