package hu.lae.infrastructure.ui.parameters.legalparameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import com.vaadin.ui.themes.ValoTheme;

import hu.lae.domain.legal.LegalIssueEvaluation;
import hu.lae.domain.legal.LegalIssueEvaluation.EvaluationEntry;
import hu.lae.domain.legal.LegalIssueType;
import hu.lae.domain.legal.LegalIssueType.Level;
import hu.lae.infrastructure.ui.component.AmountField;
import hu.lae.infrastructure.ui.component.ComboBox;

class LegalParametersTableRow {
    
    final LegalIssueType issueType;
    
    final LevelButton companyInProgressLevelButton;
    final LevelButton companyInHistoryLevelButton;
    final ComboBox<Optional<Integer>> companyLimitationYearsCombo = createLimitationYearsComboBox("Company.Limitation.Years");
    
    final LevelButton groupInProgressLevelButton;
    final LevelButton groupInHistoryLevelButton;
    final ComboBox<Optional<Integer>> groupLimitationYearsCombo = createLimitationYearsComboBox("Group.Limitation.Years");
    
    final AmountField materialityThresholdField = new AmountField(null, "Materiality.Threashold");
    
    LegalParametersTableRow(LegalIssueType issueType, EvaluationEntry companyEvaluationEntry, EvaluationEntry groupEvaluationEntry, Optional<Integer> materialityThreashold) {
        this.issueType = issueType;
        companyInProgressLevelButton = new LevelButton(companyEvaluationEntry.inProgressLevel, Level.allLevels(), "company.inProgress." + issueType);
        companyInHistoryLevelButton = new LevelButton(companyEvaluationEntry.inHistoryLevel, companyEvaluationEntry.inProgressLevel.lessOrEqualSevereLevels(), "company.inHistory." + issueType);
        companyInProgressLevelButton.addValueChangeListener(event -> inProgressLevelButtonChanged(event.getValue(), companyInHistoryLevelButton));
        companyLimitationYearsCombo.setValue(companyEvaluationEntry.limitationYears);
        
        groupInProgressLevelButton = new LevelButton(groupEvaluationEntry.inProgressLevel, Level.allLevels(), "group.inProgress." + issueType);
        groupInHistoryLevelButton = new LevelButton(groupEvaluationEntry.inHistoryLevel, groupEvaluationEntry.inProgressLevel.lessOrEqualSevereLevels(), "group.inHistory." + issueType);
        groupInProgressLevelButton.addValueChangeListener(event -> inProgressLevelButtonChanged(event.getValue(), groupInHistoryLevelButton));
        groupLimitationYearsCombo.setValue(groupEvaluationEntry.limitationYears);
        
        materialityThresholdField.setWidth("70px");
        
        if(issueType.hasMaterialityThreshold) {
            materialityThresholdField.setAmount((long)materialityThreashold.get());
        } else {
            materialityThresholdField.setVisible(false);
        }
    }
    
    private void inProgressLevelButtonChanged(Level inProgressLevel, LevelButton inHistoryLevelButton) {
		
    	Level inHistoryLevel = inHistoryLevelButton.getValue();
    	inHistoryLevelButton.setAvailableLevels(inProgressLevel.lessOrEqualSevereLevels());
    	if(inHistoryLevel.moreSevereThan(inProgressLevel)) {
    		inHistoryLevelButton.setValue(inProgressLevel);
    	}
	}

    LegalIssueEvaluation getLegalIssueEvaluation() {
    	
    	Optional<Integer> materialityThreshold = materialityThresholdField.isVisible() ? Optional.of((int)materialityThresholdField.getAmount()) : Optional.empty();
    	
    	return new LegalIssueEvaluation(issueType,
                new EvaluationEntry(companyInProgressLevelButton.getValue(), companyInHistoryLevelButton.getValue(), companyLimitationYearsCombo.getValue()),
                new EvaluationEntry(groupInProgressLevelButton.getValue(), groupInHistoryLevelButton.getValue(), groupLimitationYearsCombo.getValue()),
                materialityThreshold);
        
    }

    private static ComboBox<Optional<Integer>> createLimitationYearsComboBox(String name) {
    	List<Optional<Integer>> values = new ArrayList<>();
    	IntStream.range(1, 11).forEach(value -> values.add(Optional.of(value)));
    	values.add(Optional.empty());
    	
    	ComboBox<Optional<Integer>> comboBox = new ComboBox<>(null, name, values);
    	comboBox.setItemCaptionGenerator(item -> item.isPresent() ? item.get().toString() : "None");
    	
    	comboBox.addStyleName(ValoTheme.COMBOBOX_SMALL);
    	comboBox.setPageLength(values.size());
    	comboBox.setWidth("75px");
    	comboBox.setEmptySelectionAllowed(false);
    	comboBox.setDescription("None means no limitation");
    	comboBox.addStyleName(ValoTheme.TEXTFIELD_ALIGN_CENTER);
    	
        return comboBox;
    }
}

