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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
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
import com.ftloverdrive.model.incident.DefaultBranchCriteria;
import com.ftloverdrive.model.incident.IncidentModel;
import com.ftloverdrive.model.incident.PlotBranch;
import com.ftloverdrive.model.incident.PlotBranchCriteria;
import com.ftloverdrive.model.incident.PlotBranchCriteria.CriteriaResult;
import com.ftloverdrive.ui.ShaderLabel;
import com.ftloverdrive.ui.ShaderLabel.ShaderLabelStyle;


/**
 * Window used to display Incidents.
 * 
 * It ain't pretty, but UI code rarely is.
 */
public class IncidentDialog extends Window implements Disposable, EventListener {

	private static final WindowStyle defaultWindowStyle = new WindowStyle( new BitmapFont(), new Color(), null );
	private static final int maxChoiceCount = 10;

	public static final String SKIN_PATH = "internal://overdrive-assets/skins/incident-dialog/window.json";
	public static final String ACTOR_NAME = "IncidentDialog";

	// The window's last location.
	protected static float lastX = -1;
	protected static float lastTopY = -1;

	// Separates branches from each other
	protected int sepChoices = 0;
	// Separates the incident text from consequence box
	protected int sepTextConseq = 0;
	// Separates the consequence box from branches table
	protected int sepConseqBranches = 0;
	// Separates a branch's spoiler box from the branch label
	protected int sepBranchSpoiler = 0;

	// Size of a single repeating tile of the background
	protected float frameBodyWidth = 0;
	protected float frameBodyHeight = 0;

	protected OverdriveContext context;
	protected OVDSkin skin;

	protected ShaderLabelStyle stlNormal;
	protected ShaderLabelStyle stlHover;
	protected ShaderLabelStyle stlBlue;
	protected ShaderLabelStyle stlDisabled;

	protected ShaderLabel lblIncText;
	protected VerticalGroup vgChoices;
	protected ConsequenceBox conseqBox;

	protected PlotBranchCriteria branchCriteria;

	// Only 10 keys on the numeric keyboard, so we can only support 10 hotkeyed plot branches
	private int choiceCount = 0;
	private PlotBranch[] choices = new PlotBranch[maxChoiceCount];
	private int availableChoiceCount = 0;

	private Array<DisposeListener> disposeListeners = new Array<DisposeListener>( false, 1 );
	private boolean captureInput = false;
	private boolean dragging = false;


	public IncidentDialog( OverdriveContext srcContext ) {
		super( "", defaultWindowStyle );
		context = Pools.get( OverdriveContext.class ).obtain();
		context.init( srcContext );

		align( Align.top );
		setKeepWithinStage( true );
		setMovable( true );
		setName( ACTOR_NAME );

		branchCriteria = new DefaultBranchCriteria();

		context.getAssetManager().load( SKIN_PATH, OVDSkin.class );
		context.getAssetManager().finishLoading();
		skin = context.getAssetManager().get( SKIN_PATH, OVDSkin.class );

		ShaderLabelStyle incStyle = skin.get( "incident-text", ShaderLabelStyle.class );
		stlNormal = skin.get( "choice-normal", ShaderLabelStyle.class );
		stlHover = skin.get( "choice-hover", ShaderLabelStyle.class );
		stlBlue = skin.get( "choice-blue", ShaderLabelStyle.class );
		stlDisabled = skin.get( "choice-disabled", ShaderLabelStyle.class );

		// DistanceFieldFontShader fontShader = new DistanceFieldFontShader( 1.0f / 8.0f );

		sepChoices = skin.get( "choice-separator", Integer.class );
		sepTextConseq = skin.get( "text-conseq-separator", Integer.class );
		sepConseqBranches = skin.get( "conseq-choice-separator", Integer.class );
		sepBranchSpoiler = skin.get( "branch-spoiler-separator", Integer.class );

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
		conseqBox.align( Align.center );
		row().center().spaceBottom( sepConseqBranches );
		add( conseqBox );

		vgChoices = new VerticalGroup();
		vgChoices.align( Align.left );
		vgChoices.space( sepChoices );
		row().expandX().fillX().left();
		add( vgChoices );

		addListener( new IncidentDialogInputListener() );
		setWidth( computePreferredWidth( skin.get( "window-width", Integer.class ) ) );
	}

	public void showConseequenceBox( boolean show ) {
		conseqBox.setVisible( show );
		if ( show ) {
			getCell( lblIncText ).spaceBottom( sepTextConseq );
			getCell( conseqBox ).spaceBottom( sepConseqBranches );
		}
		else {
			getCell( lblIncText ).spaceBottom( sepConseqBranches );
			getCell( conseqBox ).spaceBottom( 0 );
		}
	}

	/**
	 * Sets the PlotBranchCriteria that will be used to determine the appearance and
	 * availability of choices.
	 */
	public void setPlotBranchCriteria( PlotBranchCriteria criteria ) {
		branchCriteria = criteria;
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
		// Determine what kind of plot branch this is, so we can set its appearance appropriately
		CriteriaResult criterion = branchCriteria.classify( context, branch );
		// Branches that get classified as blue don't show up as such if they don't show spoilers
		// TODO: Add a separate boolean for this?
		if ( criterion == CriteriaResult.BLUE && !branch.isSpoilerVisible() )
			criterion = CriteriaResult.NORMAL;
		else if ( criterion == CriteriaResult.INVISIBLE_UNAVAILABLE )
			return;

		final ShaderLabelStyle stlDefault;
		if ( criterion == CriteriaResult.BLUE ) {
			stlDefault = stlBlue;
			availableChoiceCount++;
		}
		else if ( criterion == CriteriaResult.NORMAL ) {
			stlDefault = stlNormal;
			availableChoiceCount++;
		}
		else
			stlDefault = stlDisabled;

		// Keep track of branches
		if ( choiceCount < maxChoiceCount )
			choices[choiceCount] = branch;
		choiceCount++;

		// Create the label representing the plot branch
		String choiceText = choiceCount + ". " + branch.getText();
		final ShaderLabel choice = new ShaderLabel( choiceText, stlDefault );
		choice.setAlignment( Align.top | Align.left, Align.center | Align.left );
		choice.setWrap( true );
		choice.setUserObject( branch );

		// Create the box containing consequences of this branch's incident
		final ConsequenceBox cBox = new ConsequenceBox( skin );
		boolean showBranchConseqBox = false;
		if ( branch.isSpoilerVisible() && branch.getIncidentRefId() != -1 ) {
			IncidentModel incModel = context.getReferenceManager().getObject( branch.getIncidentRefId(), IncidentModel.class );
			for ( int refId : incModel.consequenceRefIds() ) {
				Consequence conseq = context.getReferenceManager().getObject( refId, Consequence.class );
				if ( conseq.isSpoilable() ) {
					conseq.placeConsequenceActor( context, cBox );
					showBranchConseqBox = true;
				}
			}
			if ( showBranchConseqBox ) {
				cBox.setTouchable( Touchable.enabled );
				cBox.setUserObject( branch );
				if ( criterion == CriteriaResult.VISIBLE_UNAVAILABLE )
					cBox.setStyleDisabled();
				else if ( criterion == CriteriaResult.BLUE )
					cBox.setStyleBlue();
			}
		}

		// Position everything
		float availableWidth = getWidth() - getPadLeft() - getPadRight();
		if ( showBranchConseqBox )
			availableWidth += -cBox.getMinWidth() - sepBranchSpoiler;
		float w = getLabelWidth( choice, availableWidth );

		final Table tbChoice = new Table();
		tbChoice.setTouchable( Touchable.enabled );
		tbChoice.setUserObject( branch );

		tbChoice.add( choice ).left().width( w );
		if ( showBranchConseqBox )
			tbChoice.add( cBox ).left().spaceLeft( sepBranchSpoiler );

		vgChoices.addActor( tbChoice );

		InputListener listener = new InputListener() {

			public void enter( InputEvent event, float x, float y, int pointer, Actor fromActor ) {
				if ( !dragging ) {
					choice.setStyle( stlHover );
					cBox.setStyleHover();
				}
			}

			public void exit( InputEvent event, float x, float y, int pointer, Actor toActor ) {
				// Exit/enter events are also sent on touchUp/Down...
				if ( !dragging && toActor != tbChoice && toActor != choice && toActor != cBox ) {
					choice.setStyle( stlDefault );
					if ( stlDefault == stlNormal )
						cBox.setStyleNormal();
					else if ( stlDefault == stlBlue )
						cBox.setStyleBlue();
				}
			}

			@Override
			public void touchUp( InputEvent event, float x, float y, int pointer, int button ) {
				if ( !dragging ) {
					choice.setStyle( stlHover );
					cBox.setStyleHover();
				}
			}
		};

		if ( criterion != CriteriaResult.VISIBLE_UNAVAILABLE )
			tbChoice.addListener( listener );
	}

	public int countAvailableChoices() {
		return availableChoiceCount;
	}

	public void addConsequence( Consequence conseq ) {
		conseq.placeConsequenceActor( context, conseqBox );
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
		validate();
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
		validate();
		float h = tableHeight( this );
		h += ( availableChoiceCount - 1 ) * sepChoices;
		if ( h % frameBodyHeight != 0 )
			h += frameBodyHeight - h % frameBodyHeight;
		h += getPadTop() + getPadBottom();
		return h;
	}

	private float tableHeight( Table t ) {
		float result = 0;
		Array<Cell> cells = t.getCells();
		for ( int row = 0; row < t.getRows(); ++row ) {
			float maxH = 0;
			for ( int col = 0; col < t.getColumns(); ++col ) {
				int i = row * t.getColumns() + col;
				if ( i >= cells.size )
					continue;
				Cell c = cells.get( i );
				float pad = c.getComputedPadTop() + c.getComputedPadBottom();
				float cHeight = c.getMinHeight() + pad;
				if ( cHeight > maxH )
					maxH = cHeight;
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
		if ( choiceCount > maxChoiceCount )
			choiceCount = maxChoiceCount;
		for ( ; choiceCount > 0; ) {
			--choiceCount;
			choices[choiceCount] = null;
		}
		availableChoiceCount = 0;
		conseqBox.clear();
		// Hide the conseqTable to prevent it from flickering when the dialog is changing
		conseqBox.setVisible( false );
		vgChoices.clear();

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
		System.gc();
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

	private float getLabelWidth( Label l, float maxW ) {
		return l.getStyle().font.getWrappedBounds( l.getText(), maxW ).width;
	}

	private void choiceSelected( OverdriveContext context, PlotBranch branch, float x, float y ) {
		CriteriaResult criterion = branchCriteria.classify( context, branch );
		if ( criterion == CriteriaResult.VISIBLE_UNAVAILABLE )
			return;

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
		if ( event.isHandled() )
			return true;
		boolean handled = getListeners().get( 0 ).handle( event );
		if ( isVisible() && event instanceof InputEvent )
			handled = captureInput;
		return handled;
	}


	private class IncidentDialogInputListener extends InputListener {

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
		public void touchUp( InputEvent event, float x, float y, int pointer, int button ) {
			dragging = false;

			Actor a = hit( x, y, true );
			if ( a != null && a.getParent().isDescendantOf( vgChoices ) ) {
				if ( a instanceof Label || a instanceof ConsequenceBox )
					a = a.getParent();
				// Choices' listener is not informed of these events, since they're sent through
				// the dialog. Need to inform them manually.
				for ( EventListener listener : a.getListeners() )
					listener.handle( event );
			}

			if ( !captureInput )
				super.touchUp( event, x, y, pointer, button );
		}

		@Override
		public boolean touchDown( InputEvent e, float x, float y, int pointer, int button ) {
			Actor target = e.getTarget();
			dragging = false;
			if ( target.isDescendantOf( vgChoices ) && e.getButton() == Buttons.LEFT ) {
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
