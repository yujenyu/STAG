package Actions;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
// Action object for each JSONObject
public class Item {
    private Set<String> subjects;
    private Set<String> consumed;
    private Set<String> produced;
}
