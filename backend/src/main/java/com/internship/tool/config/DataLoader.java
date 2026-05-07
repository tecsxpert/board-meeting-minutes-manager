// DataLoader.java: ApplicationRunner that seeds the database with 15 realistic board minutes on startup.
// Only inserts data when the board_minutes table is empty to avoid duplicate seeding.

package com.internship.tool.config;

import com.internship.tool.entity.BoardMinutes;
import com.internship.tool.repository.BoardMinutesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")   // Skip seeding during test runs
public class DataLoader implements ApplicationRunner {

    private final BoardMinutesRepository repository;

    @Override
    public void run(ApplicationArguments args) {
        if (repository.count() > 0) {
            log.info("DataLoader: board_minutes table already has data — skipping seed.");
            return;
        }

        log.info("DataLoader: seeding 15 board meeting minutes...");
        LocalDate today = LocalDate.now();

        List<BoardMinutes> seed = List.of(

            // ── 6 PUBLISHED ─────────────────────────────────────────────────
            BoardMinutes.builder()
                .title("Q1 2026 Strategy Review")
                .meetingDate(today.minusMonths(11))
                .attendees("CEO, CFO, CTO, Head of Marketing, Board Chair")
                .content("The board reviewed Q1 performance metrics. Revenue exceeded projections by 12%. " +
                         "Key initiatives for Q2 were outlined including market expansion into APAC region. " +
                         "Budget realignment approved for cloud infrastructure upgrade.")
                .status(BoardMinutes.Status.PUBLISHED)
                .aiDescription("A strategic quarterly review covering financial performance, market expansion, and infrastructure investment.")
                .aiRecommendations("1. Accelerate APAC market entry plan by Q3.\n2. Finalize cloud vendor selection by end of Q1.\n3. Schedule monthly KPI reviews with department heads.")
                .build(),

            BoardMinutes.builder()
                .title("Annual Budget Approval 2026")
                .meetingDate(today.minusMonths(10))
                .attendees("CFO, Finance Director, CEO, Board Members (x5)")
                .content("Annual budget for FY2026 was presented and approved. Total budget set at $4.2M. " +
                         "R&D allocation increased by 18%. Sales team headcount approved for +6 FTEs. " +
                         "Marketing budget maintained with reallocation toward digital channels.")
                .status(BoardMinutes.Status.PUBLISHED)
                .aiDescription("Annual budget meeting with approved allocations across R&D, Sales, and Marketing for FY2026.")
                .aiRecommendations("1. Publish final budget breakdown to department heads within 5 business days.\n2. Set quarterly budget checkpoints.\n3. Review R&D project pipeline against new allocation.")
                .build(),

            BoardMinutes.builder()
                .title("Product Roadmap 2026 Presentation")
                .meetingDate(today.minusMonths(9))
                .attendees("CTO, VP Product, Engineering Leads, CEO")
                .content("VP Product presented the 2026 product roadmap. Key milestones: AI feature suite launch (Q2), " +
                         "mobile app v3.0 (Q3), enterprise tier pricing (Q4). Board requested bi-monthly updates. " +
                         "Compliance features prioritized for EU market entry.")
                .status(BoardMinutes.Status.PUBLISHED)
                .aiDescription("Product roadmap presentation highlighting AI features, mobile release, and enterprise pricing plans.")
                .aiRecommendations("1. Define AI feature acceptance criteria by Feb 28.\n2. Assign dedicated compliance engineer for EU requirements.\n3. Create stakeholder communication plan for roadmap milestones.")
                .build(),

            BoardMinutes.builder()
                .title("Risk & Compliance Review — Q2")
                .meetingDate(today.minusMonths(7))
                .attendees("Chief Compliance Officer, Legal Counsel, CFO, CEO, Risk Committee (x3)")
                .content("Risk register updated with 3 new high-priority items relating to data sovereignty and vendor concentration. " +
                         "GDPR compliance audit completed with no major findings. " +
                         "Cyber insurance policy renewed at improved premium.")
                .status(BoardMinutes.Status.PUBLISHED)
                .aiDescription("Quarterly risk and compliance review with GDPR audit results and updated risk register.")
                .aiRecommendations("1. Remediate vendor concentration risk by Q4 with alternative supplier agreements.\n2. Conduct tabletop cyber incident exercise in Q3.\n3. Distribute data sovereignty guidelines to all department heads.")
                .build(),

            BoardMinutes.builder()
                .title("Merger & Acquisition Feasibility Discussion")
                .meetingDate(today.minusMonths(5))
                .attendees("CEO, CFO, M&A Advisor, Board Chair, General Counsel")
                .content("Preliminary discussions on acquisition of TechStartup Inc. for estimated $8.5M. " +
                         "Due diligence checklist reviewed. Board approved proceeding to non-binding LOI stage. " +
                         "Confidentiality obligations and timeline discussed.")
                .status(BoardMinutes.Status.PUBLISHED)
                .aiDescription("Initial feasibility discussion for a potential $8.5M acquisition with LOI authorization approved.")
                .aiRecommendations("1. Engage external due diligence firm by next meeting.\n2. Establish M&A working group with legal and finance representatives.\n3. Prepare board resolution for LOI signing.")
                .build(),

            BoardMinutes.builder()
                .title("ESG & Sustainability Initiatives Update")
                .meetingDate(today.minusMonths(3))
                .attendees("CSO, CEO, ESG Committee (x4), Investor Relations Director")
                .content("Progress update on 2025 sustainability commitments. Carbon footprint reduced by 14% YoY. " +
                         "New supplier ESG scorecard launched. Board approved 2026 ESG targets including net-zero by 2030 commitment.")
                .status(BoardMinutes.Status.PUBLISHED)
                .aiDescription("ESG progress review with carbon reduction results and new 2030 net-zero commitment approved.")
                .aiRecommendations("1. Publish updated ESG report by Q3.\n2. Integrate ESG scorecard into all new supplier contracts.\n3. Commission third-party carbon audit for 2026.")
                .build(),

            // ── 5 DRAFT ─────────────────────────────────────────────────────
            BoardMinutes.builder()
                .title("Q3 2026 Financial Performance Review")
                .meetingDate(today.minusMonths(2))
                .attendees("CFO, Finance Director, CEO, Board Members")
                .content("Draft minutes pending CFO review. Revenue tracking 4% below forecast. " +
                         "Cost optimization measures proposed. Operating margin maintained at 21%.")
                .status(BoardMinutes.Status.DRAFT)
                .build(),

            BoardMinutes.builder()
                .title("Technology Infrastructure Upgrade Plan")
                .meetingDate(today.minusMonths(1).minusDays(15))
                .attendees("CTO, IT Director, CFO, CEO")
                .content("CTO presented 18-month infrastructure modernization roadmap. " +
                         "Migration from on-premise to hybrid cloud proposed. " +
                         "Estimated investment: $1.2M over 3 phases. Security architecture review scheduled.")
                .status(BoardMinutes.Status.DRAFT)
                .build(),

            BoardMinutes.builder()
                .title("New Market Entry — Latin America")
                .meetingDate(today.minusMonths(1))
                .attendees("VP Sales, Head of International, CEO, Legal Counsel")
                .content("Feasibility study for Brazil and Mexico entry presented. " +
                         "Regulatory landscape reviewed. Pilot program with 2 local distributors proposed. " +
                         "Go/no-go decision deferred to next quarter pending legal clearance.")
                .status(BoardMinutes.Status.DRAFT)
                .build(),

            BoardMinutes.builder()
                .title("Employee Equity & Compensation Review")
                .meetingDate(today.minusDays(20))
                .attendees("CHRO, Compensation Committee (x3), CEO, CFO")
                .content("Annual compensation benchmarking results presented. " +
                         "Equity refresh grants proposed for 42 key employees. " +
                         "Revised stock option vesting schedule discussed. Pending legal and finance sign-off.")
                .status(BoardMinutes.Status.DRAFT)
                .build(),

            BoardMinutes.builder()
                .title("Board Governance & Charter Refresh")
                .meetingDate(today.minusDays(7))
                .attendees("Board Chair, Company Secretary, General Counsel, Independent Directors (x3)")
                .content("Annual review of board charter, committee mandates, and director independence declarations. " +
                         "Proposed amendments to voting procedures. " +
                         "New conflict-of-interest disclosure policy reviewed — draft circulated for board approval.")
                .status(BoardMinutes.Status.DRAFT)
                .build(),

            // ── 4 ARCHIVED ──────────────────────────────────────────────────
            BoardMinutes.builder()
                .title("FY2025 Annual General Meeting")
                .meetingDate(today.minusMonths(12))
                .attendees("Full Board, Shareholders Representatives, Company Secretary, External Auditor")
                .content("Annual General Meeting for FY2025. Financial statements approved. " +
                         "Dividend of $0.42 per share declared. Board re-elections completed. " +
                         "External auditor reappointed for FY2026. Shareholder questions addressed.")
                .status(BoardMinutes.Status.ARCHIVED)
                .aiDescription("FY2025 AGM with financial statement approval, dividend declaration, and board elections.")
                .aiRecommendations("1. File AGM minutes with company registry within statutory deadline.\n2. Publish dividend payment schedule to investors.\n3. Update board composition record.")
                .build(),

            BoardMinutes.builder()
                .title("Data Breach Incident Response Review")
                .meetingDate(today.minusMonths(8))
                .attendees("CISO, CTO, Legal Counsel, CEO, PR Director")
                .content("Post-incident review of October 2024 data breach affecting 1,200 customer records. " +
                         "Root cause: misconfigured S3 bucket. Remediation completed. " +
                         "Regulatory notification submitted to ICO. Customer notification letters sent. " +
                         "Security audit commissioned — findings to be presented next quarter.")
                .status(BoardMinutes.Status.ARCHIVED)
                .aiDescription("Post-incident board review of data breach, regulatory response, and security improvements.")
                .aiRecommendations("1. Implement automated cloud configuration scanning tool.\n2. Conduct security awareness training for all engineers.\n3. Review cyber incident response playbook.")
                .build(),

            BoardMinutes.builder()
                .title("Strategic Partnership with FinTech Co.")
                .meetingDate(today.minusMonths(6))
                .attendees("CEO, VP Partnerships, Legal Counsel, CFO, Board Chair")
                .content("Proposed strategic partnership with FinTech Co. for embedded payments integration. " +
                         "Revenue sharing model: 70/30 in Company's favor. " +
                         "3-year exclusivity clause negotiated. Board approved partnership agreement signing.")
                .status(BoardMinutes.Status.ARCHIVED)
                .aiDescription("Strategic partnership approval for FinTech Co. embedded payments with 3-year exclusivity.")
                .aiRecommendations("1. Execute partnership agreement by month-end.\n2. Assign dedicated integration engineering team.\n3. Schedule joint press release for Q3.")
                .build(),

            BoardMinutes.builder()
                .title("Executive Leadership Transition Plan")
                .meetingDate(today.minusMonths(4))
                .attendees("Board Chair, Nominations Committee (x3), Outgoing CEO, HR Director")
                .content("Transition plan for incoming CEO presented. Handover period: 90 days. " +
                         "Key stakeholder introduction schedule approved. " +
                         "Outgoing CEO to remain as Strategic Advisor for 12 months. " +
                         "Announcement strategy and messaging reviewed.")
                .status(BoardMinutes.Status.ARCHIVED)
                .aiDescription("CEO succession transition plan with 90-day handover period and stakeholder communication strategy.")
                .aiRecommendations("1. Finalize transition timeline document by week's end.\n2. Schedule board introduction session for incoming CEO.\n3. Prepare internal and external announcement communications.")
                .build()
        );

        repository.saveAll(seed);
        log.info("DataLoader: seeded {} board meeting minutes successfully.", seed.size());
    }
}
