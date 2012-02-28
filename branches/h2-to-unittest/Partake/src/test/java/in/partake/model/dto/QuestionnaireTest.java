package in.partake.model.dto;

import java.util.Arrays;
import java.util.Comparator;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class QuestionnaireTest extends AbstractPartakeModelTest<Questionnaire> {

	@Test @Ignore("Not implemented yet.")
	public void testsShouldBeAdded() {
		throw new RuntimeException("Not implemented yet");
	}

	@Override
	protected Questionnaire createModel() {
		return new Questionnaire();
	}

	@Test
	public void testToComparatorQuestionNoAsc() {
		Comparator<Questionnaire> c = Questionnaire.getComparatorQuestionNoAsc();
		Questionnaire[] q = new Questionnaire[3];
		for (int i = 0; i < q.length; ++i) {
			q[i] = new Questionnaire();
			q[i].setQuestionNo(q.length - i);
		}
		Questionnaire[] expecteds = new Questionnaire[]{ q[2], q[1], q[0] };
		Arrays.sort(q, c);
		Assert.assertArrayEquals(expecteds, q);

		for (int i = 0; i < q.length; ++i) {
			q[i] = new Questionnaire();
			q[i].setQuestionNo(i);
		}
		expecteds = new Questionnaire[]{ q[0], q[1], q[2] };
		Arrays.sort(q, c);
		Assert.assertArrayEquals(expecteds, q);
	}

}
