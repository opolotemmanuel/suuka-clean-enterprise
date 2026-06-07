import React from 'react'
import styles from './AIInsightCard.module.css'

export type AIInsightCardProps = {
  title: string
  observation: string
  recommendation: string
  generatedAt?: string
  onReview?: () => void
  onApprove?: () => void
  canApprove?: boolean
}

export const AIInsightCard: React.FC<AIInsightCardProps> = ({
  title,
  observation,
  recommendation,
  generatedAt,
  onReview,
  onApprove,
  canApprove = false,
}) => {
  return (
    <div className={styles.card} role="region" aria-label={`AI insight: ${title}`}>
      <div className={styles.header}>
        <h3 className={styles.title}>{title}</h3>
        {generatedAt && <time className={styles.time}>{generatedAt}</time>}
      </div>

      <div className={styles.body}>
        <p className={styles.observation}>{observation}</p>
        <p className={styles.recommendation}>{recommendation}</p>
      </div>

      <div className={styles.actions}>
        <button className={styles.actionBtn} onClick={onReview}>
          Review Recommendation
        </button>
        {canApprove && (
          <button className={`${styles.actionBtn} ${styles.approve}`} onClick={onApprove}>
            Approve
          </button>
        )}
      </div>
    </div>
  )
}

export default AIInsightCard
