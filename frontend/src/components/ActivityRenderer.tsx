import BookingForm from './activities/BookingForm';
import RecurringScheduler from './activities/RecurringScheduler';
import ProductOrdering from './activities/ProductOrdering';
import TrackingView from './activities/TrackingView';
import ManageBooking from './activities/ManageBooking';
import ReviewsHistory from './activities/ReviewsHistory';
import CleanerJobsForm from './activities/CleanerJobsForm';
import CleanerBookingForm from './activities/CleanerBookingForm';
import CleanerAvailabilityForm from './activities/CleanerAvailabilityForm';
import CleanerSuppliesForm from './activities/CleanerSuppliesForm';
import CleanerEarningsSummary from './activities/CleanerEarningsSummary';
import ClientProfileForm from './activities/ClientProfileForm';

type Props = { activity?: string };

export default function ActivityRenderer({ activity }: Props) {
  if (!activity) return <div><em>Select an activity from the sidebar to begin.</em></div>;

  // Map known activity labels to components by matching exact labels or keywords
  if (activity === 'Jobs') return <CleanerJobsForm />;
  if (activity === 'Booking') return <CleanerBookingForm />;
  if (activity === 'Availability') return <CleanerAvailabilityForm />;
  if (activity === 'Supplies') return <CleanerSuppliesForm />;
  if (activity === 'Earnings') return <CleanerEarningsSummary />;
  if (activity === 'Client Details') return <ClientProfileForm />;
  if (activity.includes('Create a new booking')) return <BookingForm />;
  if (activity.includes('recurring') || activity.includes('Schedule recurring')) return <RecurringScheduler />;
  if (activity.includes('Browse and order') || activity.includes('Order')) return <ProductOrdering />;
  if (activity.includes('Track cleaner') || activity.includes('Service Tracking')) return <TrackingView />;
  if (activity.includes('Update, reschedule') || activity.includes('Manage Bookings')) return <ManageBooking />;
  if (activity.includes('Review cleaners') || activity.includes('Review')) return <ReviewsHistory />;

  return <div><em>Open this module from the role dashboard to load database records and permitted actions.</em></div>;
}
