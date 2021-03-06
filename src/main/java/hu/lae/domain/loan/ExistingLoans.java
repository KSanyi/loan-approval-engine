package hu.lae.domain.loan;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ExistingLoans {
    
    public static ExistingLoans createEmpty() {
        return new ExistingLoans(Collections.emptyList());
    }

    public final List<ExistingLoan> existingLoans;
    
    public ExistingLoans(List<ExistingLoan> existingLoans) {
        this.existingLoans = Collections.unmodifiableList(existingLoans);
    }
    
    public boolean isValid(LocalDate date) {
        return existingLoans.stream().filter(ExistingLoan::isLongTemLoan).allMatch(l -> !l.expiry.get().isBefore(date.plusYears(1)));
    }
    
    @Override
    public String toString() {
        return "\n" + existingLoans.stream().map(ExistingLoan::toString).collect(Collectors.joining("\n")) + "\n";
    }
    
}
