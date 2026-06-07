import FeatureCard from './components/FeatureCard';

const features = [
  {
    title: 'Instant Cleaner Dispatch',
    description: 'Locate the nearest available cleaner and book them immediately using geolocation and scheduling rules.',
  },
  {
    title: 'Consultation & Product Store',
    description: 'Book cleaning consultations and order supplies from the same platform.',
  },
  {
    title: 'AI-assisted Recommendations',
    description: 'Use Azure OpenAI to suggest cleaners, summarize reviews, and speed up customer support.',
  },
  {
    title: 'Enterprise Accounts',
    description: 'Support multi-user corporate workflows with approvals, budgets, and tenant isolation.',
  },
];

function App() {
  return (
    <div className="container">
      <header className="hero">
        <h1>Suuka Cleaning Marketplace</h1>
        <p>
          A Java + Azure platform for instant cleaner dispatch, corporate cleaning operations,
          consultation services, and product ordering in Uganda.
        </p>
      </header>

      <section className="features">
        {features.map((feature) => (
          <FeatureCard key={feature.title} title={feature.title} description={feature.description} />
        ))}
      </section>

      <section className="pitch">
        <h2>Why this project?</h2>
        <ul>
          <li>Open-source friendly stack with no runtime licensing fees.</li>
          <li>Cost-efficient multi-tenant architecture on Azure App Service.</li>
          <li>AI automation for low-risk tasks with human oversight for critical actions.</li>
          <li>Ready for enterprise, Airbnb, office, and retail cleaning workflows.</li>
        </ul>
      </section>

      <footer>
        <p>Developer pitch sample app · Backend + Frontend demo structure included.</p>
      </footer>
    </div>
  );
}

export default App;
