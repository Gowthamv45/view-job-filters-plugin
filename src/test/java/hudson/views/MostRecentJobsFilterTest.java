package hudson.views;

import hudson.model.*;
import org.junit.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static hudson.views.test.BuildMocker.build;
import static hudson.views.test.JobMocker.jobOfType;
import static hudson.views.test.JobType.*;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class MostRecentJobsFilterTest extends AbstractHudsonTest {

	@Test
	public void testWithStartTime() throws ParseException {
		List<TopLevelItem> allJobs = asList(
			jobOfType(FREE_STYLE_PROJECT).withName("job-0").withLastBuild(build().startTime("2018-01-01 00:00:00").create()).asItem(),
			jobOfType(FREE_STYLE_PROJECT).withName("job-1").withLastBuild(build().startTime("2018-01-01 01:00:00").create()).asItem(),
			jobOfType(FREE_STYLE_PROJECT).withName("job-2").withLastBuild(build().startTime("2018-01-01 02:00:00").create()).asItem(),
			jobOfType(FREE_STYLE_PROJECT).withName("job-3").withLastBuild(build().startTime("2018-01-01 03:00:00").create()).asItem(),
			jobOfType(FREE_STYLE_PROJECT).withName("job-4").withLastBuild(build().startTime("2018-01-01 04:00:00").create()).asItem()
		);

		List<TopLevelItem> addedJobs = asList(
			allJobs.get(2),
			allJobs.get(4),
			allJobs.get(0),
			allJobs.get(1),
			allJobs.get(3)
		);

		List<TopLevelItem> expectedJobs = asList(
			allJobs.get(4),
			allJobs.get(3),
			allJobs.get(2)
		);

		List<TopLevelItem> filteredJobs = new MostRecentJobsFilter(3, true).filter(addedJobs, allJobs, null);
		assertThat(filteredJobs, is(expectedJobs));
	}

	@Test
	public void testWithMaxTooLarge() throws ParseException {
		List<TopLevelItem> allJobs = asList(
			jobOfType(FREE_STYLE_PROJECT).withName("job-0").withLastBuild(build().startTime("2018-01-01 00:00:00").create()).asItem(),
			jobOfType(FREE_STYLE_PROJECT).withName("job-1").withLastBuild(build().startTime("2018-01-01 01:00:00").create()).asItem(),
			jobOfType(FREE_STYLE_PROJECT).withName("job-2").withLastBuild(build().startTime("2018-01-01 02:00:00").create()).asItem(),
			jobOfType(FREE_STYLE_PROJECT).withName("job-3").withLastBuild(build().startTime("2018-01-01 03:00:00").create()).asItem(),
			jobOfType(FREE_STYLE_PROJECT).withName("job-4").withLastBuild(build().startTime("2018-01-01 04:00:00").create()).asItem()
		);

		List<TopLevelItem> addedJobs = asList(
			allJobs.get(2),
			allJobs.get(4),
			allJobs.get(0),
			allJobs.get(1),
			allJobs.get(3)
		);

		List<TopLevelItem> expectedJobs = asList(
			allJobs.get(4),
			allJobs.get(3),
			allJobs.get(2),
			allJobs.get(1),
			allJobs.get(0)
		);

		List<TopLevelItem> filteredJobs = new MostRecentJobsFilter(10, true).filter(addedJobs, allJobs, null);
		assertThat(filteredJobs, is(expectedJobs));
	}

	@Test
	public void testWithEndTime() throws ParseException {
		List<TopLevelItem> allJobs = asList(
			jobOfType(FREE_STYLE_PROJECT).withName("job-0").withLastBuild(build().startTime("2018-01-01 00:00:00").durationInMinutes(10).create()).asItem(),
			jobOfType(FREE_STYLE_PROJECT).withName("job-1").withLastBuild(build().startTime("2018-01-01 01:00:00").durationInMinutes(90).create()).asItem(),
			jobOfType(FREE_STYLE_PROJECT).withName("job-2").withLastBuild(build().startTime("2018-01-01 02:00:00").durationInMinutes(10).create()).asItem(),
			jobOfType(FREE_STYLE_PROJECT).withName("job-3").withLastBuild(build().startTime("2018-01-01 03:00:00").durationInMinutes(90).create()).asItem(),
			jobOfType(FREE_STYLE_PROJECT).withName("job-4").withLastBuild(build().startTime("2018-01-01 04:00:00").durationInMinutes(10).create()).asItem()
		);

		List<TopLevelItem> addedJobs = asList(
			allJobs.get(2),
			allJobs.get(4),
			allJobs.get(0),
			allJobs.get(1),
			allJobs.get(3)
		);

		List<TopLevelItem> expectedJobs = asList(
			allJobs.get(3),
			allJobs.get(4),
			allJobs.get(1)
		);

		List<TopLevelItem> filteredJobs = new MostRecentJobsFilter(3, false).filter(addedJobs, allJobs, null);
		assertThat(filteredJobs, is(expectedJobs));
	}

	@Test
	public void testWithRunningBuild() throws ParseException {
		List<TopLevelItem> allJobs = asList(
			jobOfType(FREE_STYLE_PROJECT).withName("job-0").withLastBuild(build().startTime("2018-01-01 00:00:00").durationInMinutes(10).create()).asItem(),
			jobOfType(FREE_STYLE_PROJECT).withName("job-1").withLastBuild(build().startTime("2018-01-01 01:00:00").durationInMinutes(10).create()).asItem(),
			jobOfType(FREE_STYLE_PROJECT).withName("job-2").withLastBuild(build().startTime("2018-01-01 02:00:00").durationInMinutes(10).create()).asItem(),
			jobOfType(FREE_STYLE_PROJECT).withName("job-3").withLastBuild(build().startTime("2018-01-01 03:00:00").durationInMinutes(10).create()).asItem(),
			jobOfType(FREE_STYLE_PROJECT).withName("job-4").withLastBuilds(
					build().startTime("2018-01-01 04:00:00").durationInMinutes(10).building(true).create(),
					build().startTime("2018-01-01 01:30:00").durationInMinutes(10).create()
				).asItem()
		);

		List<TopLevelItem> addedJobs = asList(
			allJobs.get(2),
			allJobs.get(4),
			allJobs.get(0),
			allJobs.get(1),
			allJobs.get(3)
		);

		List<TopLevelItem> expectedJobs = asList(
			allJobs.get(3),
			allJobs.get(2),
			allJobs.get(4)
		);

		List<TopLevelItem> filteredJobs = new MostRecentJobsFilter(3, false).filter(addedJobs, allJobs, null);
		assertThat(filteredJobs, is(expectedJobs));
	}


	@Test
	public void testWithNoLastBuild() throws ParseException {
		List<TopLevelItem> allJobs = asList(
			jobOfType(FREE_STYLE_PROJECT).withName("job-0").withLastBuild(build().startTime("2018-01-01 00:00:00").create()).asItem(),
			jobOfType(FREE_STYLE_PROJECT).withName("job-1").withLastBuild(build().startTime("2018-01-01 01:00:00").create()).asItem(),
			jobOfType(FREE_STYLE_PROJECT).withName("job-2").withLastBuild(build().startTime("2018-01-01 02:00:00").create()).asItem(),
			jobOfType(FREE_STYLE_PROJECT).withName("job-3").asItem(),
			jobOfType(FREE_STYLE_PROJECT).withName("job-4").withLastBuild(build().startTime("2018-01-01 04:00:00").create()).asItem()
		);

		List<TopLevelItem> addedJobs = asList(
				allJobs.get(2),
				allJobs.get(4),
				allJobs.get(0),
				allJobs.get(1),
				allJobs.get(3)
		);

		List<TopLevelItem> expectedJobs = asList(
				allJobs.get(4),
				allJobs.get(2),
				allJobs.get(1)
		);

		List<TopLevelItem> filteredJobs = new MostRecentJobsFilter(3, true).filter(addedJobs, allJobs, null);
		assertThat(filteredJobs, is(expectedJobs));
	}


	@Test
	public void testWithNotAJob() throws ParseException {
		List<TopLevelItem> allJobs = asList(
				jobOfType(FREE_STYLE_PROJECT).withName("job-0").withLastBuild(build().startTime("2018-01-01 00:00:00").create()).asItem(),
				mock(TopLevelItem.class),
				jobOfType(FREE_STYLE_PROJECT).withName("job-2").withLastBuild(build().startTime("2018-01-01 02:00:00").create()).asItem(),
				mock(TopLevelItem.class),
				jobOfType(FREE_STYLE_PROJECT).withName("job-4").withLastBuild(build().startTime("2018-01-01 04:00:00").create()).asItem(),
				mock(TopLevelItem.class)
		);

		List<TopLevelItem> addedJobs = asList(
				allJobs.get(2),
				allJobs.get(5),
				allJobs.get(4),
				allJobs.get(0),
				allJobs.get(1),
				allJobs.get(3)
		);

		List<TopLevelItem> expectedJobs = asList(
				allJobs.get(5),
				allJobs.get(1),
				allJobs.get(3),
				allJobs.get(4),
				allJobs.get(2),
				allJobs.get(0)
		);

		List<TopLevelItem> filteredJobs = new MostRecentJobsFilter(6, true).filter(addedJobs, allJobs, null);
		assertThat(filteredJobs, is(expectedJobs));
	}


	@Test
	public void testConfigRoundtrip() throws Exception {
		testConfigRoundtrip(
				"view-1",
				new MostRecentJobsFilter(5, false)
		);

		testConfigRoundtrip(
				"view-2",
				new MostRecentJobsFilter(1, true),
				new MostRecentJobsFilter(3, false)
		);
	}

	private void testConfigRoundtrip(String viewName, MostRecentJobsFilter... filters) throws Exception {
		List<MostRecentJobsFilter> expectedFilters = new ArrayList<MostRecentJobsFilter>();
		for (MostRecentJobsFilter filter: filters) {
			expectedFilters.add(new MostRecentJobsFilter(filter.getMaxToInclude(), filter.isCheckStartTime()));
		}

		ListView view = createFilteredView(viewName, filters);
		j.configRoundtrip(view);

		ListView viewAfterRoundtrip = (ListView)j.getInstance().getView(viewName);
		assertFilterEquals(expectedFilters, viewAfterRoundtrip.getJobFilters());

		viewAfterRoundtrip.save();
		j.getInstance().reload();

		ListView viewAfterReload = (ListView)j.getInstance().getView(viewName);
		assertFilterEquals(expectedFilters, viewAfterReload.getJobFilters());
	}

	private void assertFilterEquals(List<MostRecentJobsFilter> expectedFilters, List<ViewJobFilter> actualFilters) {
		assertThat(actualFilters.size(), is(expectedFilters.size()));
		for (int i = 0; i < actualFilters.size(); i++) {
			ViewJobFilter actualFilter = actualFilters.get(i);
			MostRecentJobsFilter expectedFilter = expectedFilters.get(i);
			assertThat(actualFilter, instanceOf(MostRecentJobsFilter.class));
			assertThat(((MostRecentJobsFilter)actualFilter).getMaxToInclude(), is(expectedFilter.getMaxToInclude()));
			assertThat(((MostRecentJobsFilter)actualFilter).isCheckStartTime(), is(expectedFilter.isCheckStartTime()));
		}
	}
}
