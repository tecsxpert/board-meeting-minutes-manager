package com.internship.tool.config;

import com.internship.tool.entity.MeetingMinutes;
import com.internship.tool.entity.User;
import com.internship.tool.repository.MeetingMinutesRepository;
import com.internship.tool.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final MeetingMinutesRepository meetingMinutesRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUsers();
        seedMeetingMinutes();
    }

    private void seedUsers() {
        if (userRepository.count() == 0) {
            List<User> users = List.of(
                User.builder().username("admin").email("admin@boardmeeting.com")
                    .password(passwordEncoder.encode("admin123")).role("ADMIN").build(),
                User.builder().username("john.doe").email("john.doe@boardmeeting.com")
                    .password(passwordEncoder.encode("password123")).role("USER").build(),
                User.builder().username("sarah.smith").email("sarah.smith@boardmeeting.com")
                    .password(passwordEncoder.encode("password123")).role("USER").build()
            );
            userRepository.saveAll(users);
            log.info("Seeded {} users", users.size());
        }
    }

    private void seedMeetingMinutes() {
        if (meetingMinutesRepository.count() == 0) {
            List<MeetingMinutes> meetings = List.of(

                // PUBLISHED meetings
                MeetingMinutes.builder()
                    .title("Q1 2026 Board Review")
                    .meetingDate(LocalDate.of(2026, 1, 15))
                    .attendees("John Doe, Sarah Smith, Mike Johnson, Emily Davis")
                    .agenda("1. Q1 Financial Review\n2. Product Roadmap\n3. HR Updates")
                    .content("The board reviewed Q1 financial performance. Revenue targets were met at 98%. Product roadmap approved for Q2. Three new hires confirmed for engineering team.")
                    .status("PUBLISHED").isDeleted(false).build(),

                MeetingMinutes.builder()
                    .title("Annual Strategy Planning 2026")
                    .meetingDate(LocalDate.of(2026, 1, 22))
                    .attendees("John Doe, Sarah Smith, Robert Brown, Lisa Wilson")
                    .agenda("1. 2026 Goals\n2. Budget Allocation\n3. Key Initiatives")
                    .content("Annual strategy session completed. Key goals set for 2026 including 30% revenue growth, expansion to 3 new markets, and launch of AI-powered product suite.")
                    .status("PUBLISHED").isDeleted(false).build(),

                MeetingMinutes.builder()
                    .title("Product Launch Review - BoardAI")
                    .meetingDate(LocalDate.of(2026, 2, 5))
                    .attendees("Sarah Smith, Mike Johnson, Emily Davis, Tom Clark")
                    .agenda("1. Launch Metrics\n2. Customer Feedback\n3. Next Steps")
                    .content("BoardAI product launch reviewed. 500 signups in first week. Customer satisfaction score 4.7/5. Marketing team to increase social media presence.")
                    .status("PUBLISHED").isDeleted(false).build(),

                MeetingMinutes.builder()
                    .title("Q2 Financial Planning")
                    .meetingDate(LocalDate.of(2026, 2, 18))
                    .attendees("John Doe, Robert Brown, Lisa Wilson")
                    .agenda("1. Q2 Budget\n2. Investment Plans\n3. Cost Reduction")
                    .content("Q2 budget of $2.5M approved. Investment in AI infrastructure prioritized. Cost reduction target of 15% set for operations team.")
                    .status("PUBLISHED").isDeleted(false).build(),

                MeetingMinutes.builder()
                    .title("Security & Compliance Review")
                    .meetingDate(LocalDate.of(2026, 2, 25))
                    .attendees("John Doe, Sarah Smith, Security Team")
                    .agenda("1. Security Audit Results\n2. Compliance Updates\n3. Action Items")
                    .content("Annual security audit completed. 3 medium vulnerabilities identified and patched. GDPR compliance confirmed. New security training mandatory for all staff.")
                    .status("PUBLISHED").isDeleted(false).build(),

                MeetingMinutes.builder()
                    .title("Engineering Team Quarterly Review")
                    .meetingDate(LocalDate.of(2026, 3, 10))
                    .attendees("Mike Johnson, Emily Davis, Tom Clark, Dev Team")
                    .agenda("1. Sprint Velocity\n2. Technical Debt\n3. Hiring Plan")
                    .content("Engineering velocity improved by 20% this quarter. Technical debt reduced by 35%. 5 new engineers to be hired in Q2. Microservices migration on track.")
                    .status("PUBLISHED").isDeleted(false).build(),

                MeetingMinutes.builder()
                    .title("Customer Success Board Meeting")
                    .meetingDate(LocalDate.of(2026, 3, 20))
                    .attendees("Sarah Smith, Lisa Wilson, Customer Success Team")
                    .agenda("1. NPS Score\n2. Churn Analysis\n3. Retention Strategy")
                    .content("NPS score improved to 72 from 65. Churn rate reduced to 2.1%. New retention program launched targeting enterprise customers. 95% renewal rate achieved.")
                    .status("PUBLISHED").isDeleted(false).build(),

                MeetingMinutes.builder()
                    .title("Partnership & Alliances Review")
                    .meetingDate(LocalDate.of(2026, 3, 28))
                    .attendees("John Doe, Robert Brown, Business Dev Team")
                    .agenda("1. New Partnerships\n2. Alliance Performance\n3. Expansion Plans")
                    .content("3 new strategic partnerships signed with AWS, Microsoft and Salesforce. Alliance revenue up 45%. Expansion into European market planned for Q3.")
                    .status("PUBLISHED").isDeleted(false).build(),

                MeetingMinutes.builder()
                    .title("Q1 Results Presentation to Stakeholders")
                    .meetingDate(LocalDate.of(2026, 4, 5))
                    .attendees("Full Board, Investors, Key Stakeholders")
                    .agenda("1. Q1 Results\n2. Market Position\n3. Investor Q&A")
                    .content("Q1 results presented to stakeholders. Revenue of $8.2M exceeded target by 12%. Market share grew to 18%. Investors approved additional funding round of $15M.")
                    .status("PUBLISHED").isDeleted(false).build(),

                MeetingMinutes.builder()
                    .title("HR Policy Update Meeting")
                    .meetingDate(LocalDate.of(2026, 4, 10))
                    .attendees("John Doe, HR Team, Department Heads")
                    .agenda("1. Remote Work Policy\n2. Benefits Update\n3. Performance Review Process")
                    .content("New hybrid work policy approved — 3 days office, 2 days remote. Enhanced health benefits package approved. New quarterly performance review process introduced.")
                    .status("PUBLISHED").isDeleted(false).build(),

                // DRAFT meetings
                MeetingMinutes.builder()
                    .title("Q2 2026 Mid-Quarter Review")
                    .meetingDate(LocalDate.of(2026, 4, 14))
                    .attendees("John Doe, Sarah Smith, Mike Johnson")
                    .agenda("1. Q2 Progress\n2. Risks\n3. Adjustments")
                    .content("Mid-quarter review shows 65% of Q2 targets achieved. Supply chain risks identified. Marketing budget reallocation proposed.")
                    .status("DRAFT").isDeleted(false).build(),

                MeetingMinutes.builder()
                    .title("AI Integration Strategy Meeting")
                    .meetingDate(LocalDate.of(2026, 4, 17))
                    .attendees("Tech Team, Product Team, John Doe")
                    .agenda("1. AI Roadmap\n2. Model Selection\n3. Implementation Timeline")
                    .content("AI integration strategy discussed. LLaMA-3 selected as primary model. Integration timeline set for 8 weeks. Budget of $200K approved for AI infrastructure.")
                    .status("DRAFT").isDeleted(false).build(),

                MeetingMinutes.builder()
                    .title("New Market Entry - Southeast Asia")
                    .meetingDate(LocalDate.of(2026, 4, 21))
                    .attendees("Robert Brown, Lisa Wilson, Regional Team")
                    .agenda("1. Market Analysis\n2. Entry Strategy\n3. Resource Requirements")
                    .content("Southeast Asia market entry plan reviewed. Singapore identified as primary entry point. Local partnerships to be established. $500K budget allocated for market entry.")
                    .status("DRAFT").isDeleted(false).build(),

                MeetingMinutes.builder()
                    .title("Board Governance Review")
                    .meetingDate(LocalDate.of(2026, 4, 24))
                    .attendees("Full Board, Legal Team")
                    .agenda("1. Governance Framework\n2. Board Composition\n3. Compliance")
                    .content("Governance framework updated to align with new regulations. Two new independent directors to be appointed. Compliance calendar reviewed and approved.")
                    .status("DRAFT").isDeleted(false).build(),

                MeetingMinutes.builder()
                    .title("Technology Infrastructure Upgrade")
                    .meetingDate(LocalDate.of(2026, 4, 28))
                    .attendees("Mike Johnson, Emily Davis, IT Team")
                    .agenda("1. Current Infrastructure\n2. Upgrade Plan\n3. Cost Analysis")
                    .content("Infrastructure upgrade plan presented. Migration to cloud-native architecture proposed. Estimated cost $1.2M over 12 months. ROI projected at 3x within 2 years.")
                    .status("DRAFT").isDeleted(false).build(),

                MeetingMinutes.builder()
                    .title("Sales Strategy Q3 Planning")
                    .meetingDate(LocalDate.of(2026, 4, 30))
                    .attendees("Sales Team, John Doe, Lisa Wilson")
                    .agenda("1. Q2 Sales Review\n2. Q3 Targets\n3. Sales Enablement")
                    .content("Q2 sales pipeline reviewed. Q3 target set at $3.5M. New sales enablement tools to be deployed. Enterprise sales team to be expanded by 5 members.")
                    .status("DRAFT").isDeleted(false).build(),

                MeetingMinutes.builder()
                    .title("Sustainability & ESG Initiative")
                    .meetingDate(LocalDate.of(2026, 5, 2))
                    .attendees("John Doe, Sarah Smith, ESG Committee")
                    .agenda("1. ESG Goals\n2. Carbon Footprint\n3. Reporting")
                    .content("ESG initiative launched. Carbon neutrality target set for 2028. Green office program approved. Annual ESG report to be published starting Q3 2026.")
                    .status("DRAFT").isDeleted(false).build(),

                MeetingMinutes.builder()
                    .title("Digital Transformation Roadmap")
                    .meetingDate(LocalDate.of(2026, 5, 3))
                    .attendees("Tech Team, Business Team, John Doe")
                    .agenda("1. Digital Strategy\n2. Key Projects\n3. Change Management")
                    .content("Digital transformation roadmap approved. 5 key projects identified including CRM upgrade, data analytics platform, and customer portal. Change management program to be launched.")
                    .status("DRAFT").isDeleted(false).build(),

                // ARCHIVED meetings
                MeetingMinutes.builder()
                    .title("2025 Annual Board Review")
                    .meetingDate(LocalDate.of(2025, 12, 15))
                    .attendees("Full Board, All Department Heads")
                    .agenda("1. 2025 Performance\n2. Lessons Learned\n3. 2026 Preview")
                    .content("2025 annual review completed. Revenue of $28M achieved, 15% above target. Key lessons learned documented. 2026 strategy preview presented to board.")
                    .status("ARCHIVED").isDeleted(false).build(),

                MeetingMinutes.builder()
                    .title("Q4 2025 Financial Close")
                    .meetingDate(LocalDate.of(2025, 12, 22))
                    .attendees("John Doe, Robert Brown, Finance Team")
                    .agenda("1. Q4 Financials\n2. Year-End Close\n3. Audit Preparation")
                    .content("Q4 financials closed successfully. Full year revenue $28M. Audit preparation completed. All financial statements signed off by board.")
                    .status("ARCHIVED").isDeleted(false).build(),

                MeetingMinutes.builder()
                    .title("Product Roadmap 2025 Review")
                    .meetingDate(LocalDate.of(2025, 11, 20))
                    .attendees("Product Team, Tech Team, Sarah Smith")
                    .agenda("1. 2025 Roadmap Review\n2. Completed Features\n3. Backlog")
                    .content("2025 product roadmap reviewed. 85% of planned features delivered. Key achievements include AI assistant, mobile app, and API marketplace. 2026 roadmap to be finalized.")
                    .status("ARCHIVED").isDeleted(false).build(),

                MeetingMinutes.builder()
                    .title("Board Retreat 2025")
                    .meetingDate(LocalDate.of(2025, 10, 10))
                    .attendees("Full Board, Senior Leadership")
                    .agenda("1. Team Building\n2. Strategic Vision\n3. Culture")
                    .content("Annual board retreat completed. Strategic vision for 2026-2028 defined. Culture initiatives launched. Team cohesion significantly improved.")
                    .status("ARCHIVED").isDeleted(false).build(),

                MeetingMinutes.builder()
                    .title("Q3 2025 Investor Update")
                    .meetingDate(LocalDate.of(2025, 10, 25))
                    .attendees("Board, Investors, John Doe")
                    .agenda("1. Q3 Results\n2. Market Update\n3. Funding Plans")
                    .content("Q3 results presented to investors. Revenue $6.8M, 8% above target. Series B funding round of $20M announced. Market expansion plans shared.")
                    .status("ARCHIVED").isDeleted(false).build(),

                MeetingMinutes.builder()
                    .title("Merger & Acquisition Review")
                    .meetingDate(LocalDate.of(2025, 9, 15))
                    .attendees("John Doe, Legal Team, Finance Team")
                    .agenda("1. M&A Targets\n2. Due Diligence\n3. Valuation")
                    .content("Three M&A targets reviewed. One target selected for further due diligence. Valuation range of $5-8M identified. Legal team to proceed with NDA and initial discussions.")
                    .status("ARCHIVED").isDeleted(false).build(),

                MeetingMinutes.builder()
                    .title("Risk Management Framework Review")
                    .meetingDate(LocalDate.of(2025, 8, 20))
                    .attendees("Risk Committee, John Doe, Legal Team")
                    .agenda("1. Risk Assessment\n2. Mitigation Strategies\n3. Insurance Review")
                    .content("Annual risk management framework reviewed. 12 key risks identified and rated. Mitigation strategies updated. Insurance coverage increased by 20%.")
                    .status("ARCHIVED").isDeleted(false).build(),

                MeetingMinutes.builder()
                    .title("IPO Preparation Discussion")
                    .meetingDate(LocalDate.of(2025, 7, 10))
                    .attendees("Full Board, Investment Bankers, Legal Team")
                    .agenda("1. IPO Timeline\n2. Valuation\n3. Readiness Assessment")
                    .content("IPO preparation discussed with investment bankers. Target valuation of $150M. IPO timeline set for Q2 2027. Readiness assessment shows 70% preparation complete.")
                    .status("ARCHIVED").isDeleted(false).build(),

                MeetingMinutes.builder()
                    .title("Brand Refresh Initiative")
                    .meetingDate(LocalDate.of(2025, 6, 5))
                    .attendees("Marketing Team, Sarah Smith, Design Agency")
                    .agenda("1. Brand Audit\n2. New Identity\n3. Rollout Plan")
                    .content("Brand refresh initiative approved. New visual identity presented and approved. Rollout plan over 3 months. Budget of $150K approved for brand refresh.")
                    .status("ARCHIVED").isDeleted(false).build(),

                MeetingMinutes.builder()
                    .title("Global Expansion Strategy 2025")
                    .meetingDate(LocalDate.of(2025, 5, 15))
                    .attendees("John Doe, Robert Brown, Regional Directors")
                    .agenda("1. Market Analysis\n2. Entry Strategy\n3. Resource Plan")
                    .content("Global expansion strategy approved. Target markets: UK, Germany, Singapore, Australia. Total investment of $3M over 18 months. First office to open in London Q3 2025.")
                    .status("ARCHIVED").isDeleted(false).build(),

                MeetingMinutes.builder()
                    .title("Founding Board Meeting")
                    .meetingDate(LocalDate.of(2025, 1, 10))
                    .attendees("Founding Team, Initial Investors")
                    .agenda("1. Company Vision\n2. Initial Strategy\n3. Governance Setup")
                    .content("Inaugural board meeting held. Company vision and mission defined. Initial strategy for first year approved. Board governance structure established. Seed funding of $2M confirmed.")
                    .status("ARCHIVED").isDeleted(false).build()
            );

            meetingMinutesRepository.saveAll(meetings);
            log.info("Seeded {} meeting minutes records", meetings.size());
        } else {
            log.info("Data already exists, skipping seeder");
        }
    }
}
